/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2013 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.wordpress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.FileUtils.ZipEntryFilter;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.wordpress.commands.WordPressCli;
import org.netbeans.modules.php.wordpress.preferences.WordPressPreferences;
import org.netbeans.modules.php.wordpress.ui.options.WordPressOptions;
import org.netbeans.modules.php.wordpress.ui.wizards.NewProjectConfigurationPanel;
import org.netbeans.modules.php.wordpress.util.Charset;
import org.netbeans.modules.php.wordpress.util.NetUtils;
import org.netbeans.modules.php.wordpress.util.WPFileUtils;
import org.netbeans.modules.php.wordpress.util.WPZipEntryFilter;
import org.netbeans.modules.php.wordpress.wpapis.WordPressVersionCheckApi;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class WordPressPhpModuleExtender extends PhpModuleExtender {

    private static final String HTTPS_API_WORDPRESS_ORG_SECRET_KEY = "https://api.wordpress.org/secret-key/1.1/salt/"; // NOI18N
    private static final String DEFINE_PATTERN = "^define\\('(.*)', *.*\\);$"; // NOI18N
    private static final String DEFINE_FORMAT_PATTERN = "define('%s', '%s');"; // NOI18N
    private static final String WP_CONFIG_PHP = "wp-config.php"; // NOI18N
    private NewProjectConfigurationPanel panel;
    private static final String WORDPRESS = "wordpress"; // NOI18N
    private static final String WP_DL_URL_DEFAULT = "https://wordpress.org/latest.zip"; // NOI18N
    private static final String DB_NAME = "DB_NAME"; // NOI18N
    private static final String DB_USER = "DB_USER"; // NOI18N
    private static final String DB_PASSWORD = "DB_PASSWORD"; // NOI18N
    private static final String DB_HOST = "DB_HOST"; // NOI18N
    private static final String DB_CHARSET = "DB_CHARSET"; // NOI18N
    private static final String DB_COLLATE = "DB_COLLATE"; // NOI18N
    private static final Set<String> SECRET_KEYS = new HashSet<>();
    private static final Set<String> CONFIG_KEYS = new HashSet<>();
    private static final Map<String, String> CONFIG_MAP = new HashMap<>();
    private boolean isInternetReachable = true;
    private String errorMessage;
    private static final Logger LOGGER = Logger.getLogger(WordPressPhpModuleExtender.class.getName());

    static {
        SECRET_KEYS.add("AUTH_KEY"); // NOI18N
        SECRET_KEYS.add("SECURE_AUTH_KEY"); // NOI18N
        SECRET_KEYS.add("LOGGED_IN_KEY"); // NOI18N
        SECRET_KEYS.add("NONCE_KEY"); // NOI18N
        SECRET_KEYS.add("AUTH_SALT"); // NOI18N
        SECRET_KEYS.add("SECURE_AUTH_SALT"); // NOI18N
        SECRET_KEYS.add("LOGGED_IN_SALT"); // NOI18N
        SECRET_KEYS.add("NONCE_SALT"); // NOI18N

        CONFIG_KEYS.add(DB_NAME);
        CONFIG_KEYS.add(DB_USER);
        CONFIG_KEYS.add(DB_PASSWORD);
        CONFIG_KEYS.add(DB_HOST);
        CONFIG_KEYS.add(DB_CHARSET);
        CONFIG_KEYS.add(DB_COLLATE);
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @NbBundle.Messages("WordPressPhpModuleExtender.no.installation=There is no available installation method.")
    @Override
    public boolean isValid() {
        String url = panel.getUrlLabel();
        String localFile = panel.getLocalFileLabel();
        if (!isInternetReachable || url.isEmpty()) {
            if (localFile.isEmpty()) {
                // disable all field
                panel.setAllEnabled(panel, false);
                panel.setAllEnabled(panel.getWpConfigPanel(), false);
                errorMessage = Bundle.WordPressPhpModuleExtender_no_installation();
                return false;
            }
        }
        errorMessage = null;
        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getWarningMessage() {
        return errorMessage;
    }

    /**
     * Unzip from url. In case of default, download from
     * http://wordpress.org/latest.zip.
     *
     * @see <a href="http://wordpress.org/">WordPress</a>
     * @param pm
     * @return file set
     * @throws
     * org.netbeans.modules.php.spi.framework.PhpModuleExtender.ExtendingException
     */
    @Override
    public Set<FileObject> extend(PhpModule pm) throws ExtendingException {
        panel.setAllEnabled(panel, false);
        panel.setAllEnabled(panel.getWpConfigPanel(), false);

        FileObject sourceDirectory = pm.getSourceDirectory();
        if (panel.useUrl()) {
            // url
            String urlPath = panel.getUrlLabel();
            try {
                // unzip
                WPFileUtils.unzip(urlPath, FileUtil.toFile(sourceDirectory), new WPZipEntryFilter(panel.getUnzipStatusTextField()));
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                throw new ExtendingException(ex.getLocalizedMessage());
            }
        } else if (panel.useLocalFile()) {
            // local file
            String path = panel.getLocalFileLabel();
            try {
                FileUtils.unzip(path, FileUtil.toFile(sourceDirectory), new ZipEntryFilterImpl());
            } catch (IOException ex) {
                throw new ExtendingException(ex.getLocalizedMessage());
            }

        } else if (panel.useWpCli()) {
            try {
                // params
                ArrayList<String> params = new ArrayList<>(2);
                WordPressOptions options = WordPressOptions.getInstance();
                String locale = options.getWpCliDownloadLocale();
                if (!StringUtils.isEmpty(locale)) {
                    params.add(String.format(WordPressCli.LOCALE_PARAM, locale));
                }
                String version = options.getWpCliDownloadVersion();
                if (!StringUtils.isEmpty(version)) {
                    params.add(String.format(WordPressCli.VERSION_PARAM, version));
                }

                // run
                WordPressCli wpCli = WordPressCli.getDefault(false);
                Future<Integer> result = wpCli.download(pm, params);
                if (result != null) {
                    result.get();
                }
            } catch (InvalidPhpExecutableException | ExecutionException ex) {
                throw new ExtendingException(ex.getLocalizedMessage());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            // #18
            sourceDirectory.refresh(true);
        } else {
            // do nothing
            return Collections.emptySet();
        }

        // set format
        if (panel.useFormatOption()) {
            WordPressPreferences.setWordPressFormat(pm);
        }

        WordPressPreferences.setEnabled(pm, true);

        Set<FileObject> files = new HashSet<>();
        if (sourceDirectory != null) {
            // create wp-config.php
            if (panel.isSelectedCreateConfig()) {
                setConfigMap();
                createWpConfig(sourceDirectory.getFileObject("wp-config-sample.php")); // NOI18N
            }
            // set files to open
            FileObject config = sourceDirectory.getFileObject(WP_CONFIG_PHP); // NOI18N
            if (config != null) {
                files.add(config);
            }
            FileObject index = sourceDirectory.getFileObject("index.php"); // NOI18N
            if (index != null) {
                files.add(index);
            }
        }

        return files;
    }

    /**
     * Get download url. If url is set to option panel or found url of user
     * language, return it. Otherwise return default url.
     *
     * @see <a href="http://wordpress.org/latest.zip">WordPress Download</a>
     * @return download url
     */
    private String getDownloadUrl() {
        String downloadUrl = WordPressOptions.getInstance().getDownloadUrl();
        if (!StringUtils.isEmpty(downloadUrl)) {
            return downloadUrl;
        }

        // get url from version check api
        String wpLocale = WordPressOptions.getInstance().getWpLocale();
        if (StringUtils.isEmpty(wpLocale)) {
            Locale locale = Locale.getDefault();
            wpLocale = locale.getLanguage();
        }
        if (!StringUtils.isEmpty(wpLocale)) {
            WordPressVersionCheckApi versionCheckApi = new WordPressVersionCheckApi(wpLocale);
            try {
                versionCheckApi.parse();
                downloadUrl = versionCheckApi.getDownload();
                // follow a redirect url
                // it seems that the redirect url is used https
                downloadUrl = downloadUrl.replace("http://", "https://"); // NOI18N
            } catch (IOException ex) {
                downloadUrl = WP_DL_URL_DEFAULT;
            }
        }
        if (!StringUtils.isEmpty(downloadUrl)) {
            return downloadUrl;
        }

        // default url
        return WP_DL_URL_DEFAULT;
    }

    private void createWpConfig(FileObject sample) {
        if (sample == null) {
            return;
        }

        PrintWriter pw = null;
        List<String> lines = null;
        try {
            lines = sample.asLines(Charset.UTF8);
            FileObject parent = sample.getParent();
            OutputStream outpuStream = parent.createAndOpen(WP_CONFIG_PHP);
            pw = new PrintWriter(new OutputStreamWriter(outpuStream, Charset.UTF8));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (pw == null) {
            return;
        }

        // write
        try {
            Pattern pattern = Pattern.compile(DEFINE_PATTERN);
            boolean isSecret = false;
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String group = matcher.group(1);
                    if (CONFIG_KEYS.contains(group)) {
                        pw.println(String.format(DEFINE_FORMAT_PATTERN, group, CONFIG_MAP.get(group)));
                        continue;
                    }
                    if (SECRET_KEYS.contains(group) && isInternetReachable) {
                        isSecret = true;
                        continue;
                    }
                }
                if (isSecret) {
                    for (String key : getSecretKey()) {
                        pw.println(key);
                    }
                    isSecret = false;
                }
                pw.println(line);
            }
        } finally {
            pw.close();
        }
    }

    /**
     * Get local file. can set only zip file.
     *
     * @return local zip file
     */
    private String getLocalFile() {
        return WordPressOptions.getInstance().getLocalFilePath();
    }

    /**
     * Get secret keys with wordpress api.
     *
     * @see <a href="https://api.wordpress.org/secret-key/1.1/salt/">WordPress
     * API</a>
     * @return
     */
    private List<String> getSecretKey() {
        List<String> keys = new ArrayList<>();
        try {
            URL url = new URL(HTTPS_API_WORDPRESS_ORG_SECRET_KEY);
            URLConnection connection = url.openConnection();
            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpsConnection.getInputStream(), Charset.UTF8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        keys.add(line);
                    }
                }
            }
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        return keys;
    }

    private synchronized void setConfigMap() {
        CONFIG_MAP.put(DB_NAME, panel.getDbName());
        CONFIG_MAP.put(DB_USER, panel.getDbUser());
        CONFIG_MAP.put(DB_PASSWORD, panel.getDbPassword());
        CONFIG_MAP.put(DB_HOST, panel.getDbHost());
        CONFIG_MAP.put(DB_CHARSET, panel.getDbCharset());
        CONFIG_MAP.put(DB_COLLATE, panel.getDbCollate());
    }

    /**
     * Get panel
     *
     * @return
     */
    private synchronized NewProjectConfigurationPanel getPanel() {
        if (panel == null) {
            panel = new NewProjectConfigurationPanel();
            String url = getDownloadUrl();
            if (!NetUtils.isInternetReachable(url)) {
                isInternetReachable = false;
                panel.setEnabledUrl(false);
            }
            panel.setUrlLabel(url);
            panel.setLocalFileLabel(getLocalFile());
        }
        return panel;
    }

    private static class ZipEntryFilterImpl implements ZipEntryFilter {

        public ZipEntryFilterImpl() {
        }

        @Override
        public boolean accept(ZipEntry ze) {
            if (ze.isDirectory() && ze.getName().equals(WORDPRESS)) {
                return false;
            }
            return true;
        }

        @Override
        public String getName(ZipEntry ze) {
            String name = ze.getName();
            if (name.startsWith(WORDPRESS)) {
                name = name.replaceFirst(WORDPRESS, ""); // NOI18N
            }
            return name;
        }
    }
}
