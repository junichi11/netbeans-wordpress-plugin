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
package org.netbeans.modules.php.wordpress.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.netbeans.modules.php.api.editor.PhpBaseElement;
import org.netbeans.modules.php.api.editor.PhpClass;
import org.netbeans.modules.php.api.editor.PhpVariable;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public class WordPressEditorExtender extends EditorExtender {

    /**
     * global variables
     *
     * @see <a href="http://codex.wordpress.org/Global_Variables">Global
     * Variables</a>
     */
    private static final Map<String, String> GLOBAL_MAP = new HashMap<>();
    private static final List<PhpBaseElement> ELEMENTS = new LinkedList<>();

    static {
        // object
        GLOBAL_MAP.put("$post", "WP_Post"); // NOI18N
        GLOBAL_MAP.put("$wpdb", "wpdb"); // NOI18N
        GLOBAL_MAP.put("$wp_admin_bar", "WP_Admin_Bar"); // NOI18N
        GLOBAL_MAP.put("$wp_query", "WP_Query"); // NOI18N
        GLOBAL_MAP.put("$wp_roles", "WP_Roles"); // NOI18N
        GLOBAL_MAP.put("$wp_rewrite", "WP_Rewrite"); // NOI18N
        GLOBAL_MAP.put("$wp", "WP"); // NOI18N
        GLOBAL_MAP.put("$wp_locale", "WP_Locale"); // NOI18N
        // others
        GLOBAL_MAP.put("$currentday", "string"); // NOI18N
        GLOBAL_MAP.put("$currentmonth", "string"); // NOI18N
        GLOBAL_MAP.put("$page", "int"); // NOI18N
        GLOBAL_MAP.put("$pages", "int"); // NOI18N
        GLOBAL_MAP.put("$multipage", "boolean"); // NOI18N
        GLOBAL_MAP.put("$more", "boolean"); // NOI18N
        GLOBAL_MAP.put("$numpages", "int"); // NOI18N
        GLOBAL_MAP.put("$is_iphone", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_chrome", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_safari", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_NS4", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_opera", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_macIE", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_winIE", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_gecko", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_lynx", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_IE", "boolean"); // NOI18N

        GLOBAL_MAP.put("$is_apache", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_IIS", "boolean"); // NOI18N
        GLOBAL_MAP.put("$is_iis7", "boolean"); // NOI18N

        GLOBAL_MAP.put("$wp_version", "string"); // NOI18N
        GLOBAL_MAP.put("$wp_db_version", "int"); // NOI18N
        GLOBAL_MAP.put("$tynymce_version", "string"); // NOI18N
        GLOBAL_MAP.put("$manifest_version", "string"); // NOI18N
        GLOBAL_MAP.put("$required_php_version", "string"); // NOI18N
        GLOBAL_MAP.put("$required_mysql_version", "string"); // NOI18N

        GLOBAL_MAP.put("$pagenow", "string"); // NOI18N
        GLOBAL_MAP.put("$allowedposttags", "array"); // NOI18N
        GLOBAL_MAP.put("$allowedtags", "array"); // NOI18N

        Set<String> keySet = GLOBAL_MAP.keySet();
        for (String key : keySet) {
            String clazz = GLOBAL_MAP.get(key);
            PhpVariable phpVariable = new PhpVariable(key, new PhpClass(clazz, clazz));
            ELEMENTS.add(phpVariable);
        }
        // $authordata
        PhpClass authorClass = new PhpClass("stdClass", "stdClass"); // NOI18N
        authorClass.addField("$ID", "$ID"); // NOI18N
        authorClass.addField("$user_login", "$user_login"); // NOI18N
        authorClass.addField("$user_pass", "$user_pass"); // NOI18N
        authorClass.addField("$user_nicename", "$user_nicename"); // NOI18N
        authorClass.addField("$user_email", "$user_email"); // NOI18N
        authorClass.addField("$user_url", "$user_url"); // NOI18N
        authorClass.addField("$user_registered", "$user_registered"); // NOI18N
        authorClass.addField("$user_activation_key", "$user_activation_key"); // NOI18N
        authorClass.addField("$user_status", "$user_status"); // NOI18N
        authorClass.addField("$display_name", "$display_name"); // NOI18N
        ELEMENTS.add(new PhpVariable("$authordata", authorClass)); // NOI18N
    }

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        PhpModule phpModule = PhpModule.Factory.forFileObject(fo);
        // check whether project is WordPress
        if (!WPUtils.isWP(phpModule)) {
            return Collections.emptyList();
        }
        return ELEMENTS;
    }
}
