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
package org.netbeans.modules.php.wordpress.ui.actions;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.ui.wizards.CreateUnderscoresThemePanel;
import org.netbeans.modules.php.wordpress.util.Charset;
import org.netbeans.modules.php.wordpress.util.UnderscoresUtils;
import org.netbeans.modules.php.wordpress.util.UnderscoresZipEntryFilter;
import org.netbeans.modules.php.wordpress.util.WPFileUtils;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.netbeans.modules.php.wordpress.util.WordPressUnzipException;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Use underscores(_s) for creating theme.
 *
 * @see <a href="https://github.com/Automattic/_s">github:underscores</a>
 * @see <a href="http://underscores.me/">underscores.me</a>
 * @author junichi11
 */
public class CreateUnderscoresThemeAction extends BaseAction {

    private static final long serialVersionUID = -5290582852489607026L;
    private static final Logger LOGGER = Logger.getLogger(CreateUnderscoresThemeAction.class.getName());
    private static final String UNDERSCORES_ZIP_URL = "https://github.com/Automattic/_s/archive/master.zip"; // NOI18N
    private String _s;
    private String _s_;
    private String themeName;
    private String author;
    private String authorUri;
    private String description;
    private static final CreateUnderscoresThemeAction INSTANCE = new CreateUnderscoresThemeAction();

    private CreateUnderscoresThemeAction() {
    }

    public static CreateUnderscoresThemeAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @NbBundle.Messages("LBL_CreateUnderscoresThemeAction=Underscores")
    @Override
    protected String getPureName() {
        return Bundle.LBL_CreateUnderscoresThemeAction();
    }

    @Override
    protected void actionPerformed(PhpModule pm) {
        // called via shortcut
        if (!WPUtils.isWP(pm)) {
            return;
        }
        // create folder
        FileObject themesDirectory = WordPressModule.Factory.forPhpModule(pm).getThemesDirectory();
        if (themesDirectory == null) {
            LOGGER.log(Level.WARNING, "themes directory don't exist!");
            return;
        }

        // get existing theme names
        Set<String> existingThemeNames = getExistingThemeNames(themesDirectory);

        // create dialog
        CreateUnderscoresThemePanel panel = new CreateUnderscoresThemePanel(existingThemeNames);
        panel.showDialog();
        if (!panel.isOK()) {
            return;
        }

        // click OK
        String name = panel.getThemeName().trim();
        author = panel.getAuthor().trim();
        authorUri = panel.getAuthorUri().trim();
        description = panel.getDescription().trim();

        // create folder name, function prefix, theme name
        String themeFolerName = UnderscoresUtils.toFolderName(name);
        _s = UnderscoresUtils.toTextDomain(name);
        _s_ = UnderscoresUtils.toFunctionName(name);
        themeName = name;

        FileObject themeFolder = null;
        try {
            themeFolder = themesDirectory.createFolder(themeFolerName);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }

        if (themeFolder == null) {
            return;
        }
        final FileObject theme = themeFolder;

        // display progress bar
        RequestProcessor.getDefault().post(() -> {
            ProgressHandle handle = ProgressHandle.createHandle("Createing theme", () -> true);
            try {
                handle.start();
                if (!unzipAndReplace(theme)) {
                    LOGGER.log(Level.WARNING, "fail: create wp theme");
                }
            } finally {
                handle.finish();
            }
        });
    }

    /**
     * At first Unzip Underscores from github and replace some values.
     *
     * @param themeFolder
     * @return
     */
    private boolean unzipAndReplace(FileObject themeFolder) {
        // unzip
        try {
            WPFileUtils.unzip(UNDERSCORES_ZIP_URL, FileUtil.toFile(themeFolder), new UnderscoresZipEntryFilter());
            themeFolder.refresh();
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        } catch (IOException | WordPressUnzipException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
        FileObject[] children = themeFolder.getChildren();
        if (children.length == 0) {
            return false;
        }
        // replace
        replace(themeFolder);
        return true;
    }

    /**
     * Replace : theme name, function name, e.t.c.
     *
     * @param directory
     */
    private void replace(FileObject directory) {
        FileObject[] children = directory.getChildren();
        for (FileObject child : children) {
            if (child.isFolder()) {
                replace(child);
                continue;
            }
            String ext = child.getExt();
            if (ext.equals("md") || ext.equals("txt")) { // NOI18N
                continue;
            }
            try {
                List<String> lines = child.asLines();
                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(child.getOutputStream(), Charset.UTF8))) {
                    for (String line : lines) {
                        line = line.replaceAll("_s_", _s_); // NOI18N
                        line = line.replaceAll(" _s", " " + themeName); // NOI18N
                        line = line.replaceAll("'_s'", "'" + _s + "'"); // NOI18N
                        if (child.getNameExt().equals("style.css")) { // NOI18N
                            if (line.startsWith("Author:")) { // NOI18N
                                line = "Author: " + author; // NOI18N
                            } else if (line.startsWith("Author URI:")) { // NOI18N
                                line = "Author URI: " + authorUri; // NOI18N
                            } else if (line.startsWith("Description:")) { // NOI18N
                                line = "Description: " + description; // NOI18N
                            }
                        }
                        pw.println(line);
                    }
                }
            } catch (FileAlreadyLockedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }

    /**
     * Get existing theme names.
     *
     * @param themesDirectory
     * @return theme names
     */
    private Set<String> getExistingThemeNames(FileObject themesDirectory) {
        Set<String> existingThemeNames = new HashSet<>();
        for (FileObject child : themesDirectory.getChildren()) {
            if (child.isFolder()) {
                existingThemeNames.add(child.getName());
            }
        }
        return existingThemeNames;
    }
}
