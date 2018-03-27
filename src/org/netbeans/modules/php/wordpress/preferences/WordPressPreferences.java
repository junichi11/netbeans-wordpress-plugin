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
package org.netbeans.modules.php.wordpress.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import org.netbeans.modules.editor.indent.api.IndentUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.indent.FmtOptions;

/**
 *
 * @author junichi11
 */
public class WordPressPreferences {

    private static final List<String> WITHIN_OPTIONS = new ArrayList<>();
    private static final String ENABLED = "enabled"; // NOI18N
    private static final String CUSTOM_CONTENT_NAME = "custom-content-name"; // NOI18N
    private static final String WP_CONTENT_PATH = "custom-content-path"; // NOI18N
    private static final String WP_ROOT = "wp-root"; // NOI18N
    private static final String PLUGINS = "plugins"; // NOI18N
    private static final String THEMES = "themes"; // NOI18N

    private WordPressPreferences() {
    }

    public static boolean isEnabled(PhpModule phpModule) {
        return getPreferences(phpModule).getBoolean(ENABLED, false);
    }

    public static void setEnabled(PhpModule phpModule, boolean isEnabled) {
        getPreferences(phpModule).putBoolean(ENABLED, isEnabled);
    }

    public static String getCustomContentName(PhpModule phpModule) {
        return getPreferences(phpModule).get(CUSTOM_CONTENT_NAME, "wp-content"); // NOI18N
    }

    public static void setCustomContentName(PhpModule phpModule, String name) {
        getPreferences(phpModule).put(CUSTOM_CONTENT_NAME, name);
    }

    public static String getWpContentPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(WP_CONTENT_PATH, ""); // NOI18N
    }

    public static void setWpContentPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(WP_CONTENT_PATH, path);
    }

    public static String getWordPressRootPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(WP_ROOT, ""); // NOI18N
    }

    public static void setWordPressRootPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(WP_ROOT, path);
    }

    public static String getPluginsPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(PLUGINS, ""); // NOI18N
    }

    public static void setPluginsPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(PLUGINS, path);
    }

    public static String getThemesPath(PhpModule phpModule) {
        return getPreferences(phpModule).get(THEMES, ""); // NOI18N
    }

    public static void setThemesPath(PhpModule phpModule, String path) {
        getPreferences(phpModule).put(THEMES, path);
    }

    private static Preferences getPreferences(PhpModule phpModule) {
        return phpModule.getPreferences(WordPressPreferences.class, true);
    }

    // formatting
    static {
        WITHIN_OPTIONS.add(FmtOptions.SPACE_WITHIN_ARRAY_DECL_PARENS);
        WITHIN_OPTIONS.add(FmtOptions.SPACE_WITHIN_CATCH_PARENS);
        WITHIN_OPTIONS.add(FmtOptions.SPACE_WITHIN_FOR_PARENS);
        WITHIN_OPTIONS.add(FmtOptions.SPACE_WITHIN_IF_PARENS);
        WITHIN_OPTIONS.add(FmtOptions.SPACE_WITHIN_METHOD_CALL_PARENS);
        WITHIN_OPTIONS.add(FmtOptions.SPACE_WITHIN_METHOD_DECL_PARENS);
        WITHIN_OPTIONS.add(FmtOptions.SPACE_WITHIN_SWITCH_PARENS);
        WITHIN_OPTIONS.add(FmtOptions.SPACE_WITHIN_WHILE_PARENS);
    }

    public static void setWordPressFormat(PhpModule phpModule) {
        getIndentPreferences(phpModule).node("CodeStyle").put("usedProfile", "project"); // NOI18N
        Preferences p = getIndentPhpPreferences(phpModule);
        p.putBoolean("expand-tabs", false); // NOI18N
        p.putInt("indent-shift-width", 4); // NOI18N
        p.putInt("spaces-per-tab", 4); // NOI18N
        for (String option : WITHIN_OPTIONS) {
            p.putBoolean(option, true);
        }
        p.putInt("tab-size", 4); // NOI18N
        p.putInt("text-limit-width", 80); // NOI18N
        p.put("text-line-wrap", "none"); // NOI18N
    }

    private static Preferences getIndentPreferences(PhpModule phpModule) {
        return phpModule.getPreferences(IndentUtils.class, true);
    }

    private static Preferences getIndentPhpPreferences(PhpModule phpModule) {
        return phpModule.getPreferences(IndentUtils.class, true).node("text/x-php5").node("CodeStyle").node("project"); // NOI18N
    }
}
