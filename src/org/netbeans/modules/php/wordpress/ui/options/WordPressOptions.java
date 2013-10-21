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
package org.netbeans.modules.php.wordpress.ui.options;

import java.util.prefs.Preferences;
import org.netbeans.modules.php.api.util.UiUtils;
import org.openide.util.NbPreferences;

/**
 *
 * @author junichi11
 */
public class WordPressOptions {

    public static final String OPTIONS_SUBPATH = "WordPress"; // NOI18N
    private static final WordPressOptions INSTANCE = new WordPressOptions();
    private static final String PREFERENCES_PATH = "wordpress"; // NOI18N
    private static final String DOWNLOAD_URL = "download.url"; // NOI18N
    private static final String LOCAL_FILE_PATH = "local.file.path"; // NOI18N
    private static final String WP_CLI_PATH = "wp-cli.path"; // NOI18N
    private static final String WP_CLI_DOWNLOAD_LOCALE = "wp-cli.download.locale"; // NOI18N
    private static final String WP_CLI_DOWNLOAD_VERSION = "wp-cli.download.version"; // NOI18N
    private static final String WP_CLI_GET_COMMANDS_ON_BOOT = "wp-cli.get.commands.on.boot"; // NOI18N
    private static final String WP_CLI_COMMAND_LIST = "wp-cli.command.list"; // NOI18N

    private WordPressOptions() {
    }

    public static String getOptionsPath() {
        return UiUtils.OPTIONS_PATH + "/" + WordPressOptions.OPTIONS_SUBPATH; // NOI18N
    }

    public static WordPressOptions getInstance() {
        return INSTANCE;
    }

    public synchronized String getDownloadUrl() {
        return getPreferences().get(DOWNLOAD_URL, ""); // NOI18N
    }

    public void setDownloadUrl(String url) {
        getPreferences().put(DOWNLOAD_URL, url);
    }

    public synchronized String getLocalFilePath() {
        return getPreferences().get(LOCAL_FILE_PATH, ""); // NOI18N
    }

    public void setLocalFilePath(String path) {
        getPreferences().put(LOCAL_FILE_PATH, path);
    }

    public String getWpCliPath() {
        return getPreferences().get(WP_CLI_PATH, ""); // NOI18N
    }

    public void setWpCliPath(String path) {
        getPreferences().put(WP_CLI_PATH, path);
    }

    public String getWpCliDownloadLocale() {
        return getPreferences().get(WP_CLI_DOWNLOAD_LOCALE, ""); // NOI18N
    }

    public void setWpCliDownloadLocale(String locale) {
        getPreferences().put(WP_CLI_DOWNLOAD_LOCALE, locale);
    }

    public String getWpCliDownloadVersion() {
        return getPreferences().get(WP_CLI_DOWNLOAD_VERSION, ""); // NOI18N
    }

    public void setWpCliDownloadVersion(String version) {
        getPreferences().put(WP_CLI_DOWNLOAD_VERSION, version);
    }

    public boolean getWpCliGetCommandsOnBoot() {
        return getPreferences().getBoolean(WP_CLI_GET_COMMANDS_ON_BOOT, true);
    }

    public void setWpCliGetCommandsOnBoot(boolean get) {
        getPreferences().putBoolean(WP_CLI_GET_COMMANDS_ON_BOOT, get);
    }

    public String getWpCliCommandList() {
        return getPreferences().get(WP_CLI_COMMAND_LIST, null);
    }

    public void setWpCliCommandList(String text) {
        getPreferences().put(WP_CLI_COMMAND_LIST, text);
    }

    private Preferences getPreferences() {
        return NbPreferences.forModule(WordPressOptions.class).node(PREFERENCES_PATH);
    }

}
