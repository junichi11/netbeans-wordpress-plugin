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
    private static final Map<String, String> globalMap = new HashMap<String, String>();
    private static final List<PhpBaseElement> elements = new LinkedList<PhpBaseElement>();

    static {
        // object
        globalMap.put("$post", "WP_Post");
        globalMap.put("$wpdb", "wpdb");
        globalMap.put("$wp_admin_bar", "WP_Admin_Bar");
        globalMap.put("$wp_query", "WP_Query");
        globalMap.put("$wp_roles", "WP_Roles");
        globalMap.put("$wp_rewrite", "WP_Rewrite");
        globalMap.put("$wp", "WP");
        globalMap.put("$wp_locale", "WP_Locale");
        // others
        globalMap.put("$currentday", "string");
        globalMap.put("$currentmonth", "string");
        globalMap.put("$page", "int");
        globalMap.put("$pages", "int");
        globalMap.put("$multipage", "boolean");
        globalMap.put("$more", "boolean");
        globalMap.put("$numpages", "int");
        globalMap.put("$is_iphone", "boolean");
        globalMap.put("$is_chrome", "boolean");
        globalMap.put("$is_safari", "boolean");
        globalMap.put("$is_NS4", "boolean");
        globalMap.put("$is_opera", "boolean");
        globalMap.put("$is_macIE", "boolean");
        globalMap.put("$is_winIE", "boolean");
        globalMap.put("$is_gecko", "boolean");
        globalMap.put("$is_lynx", "boolean");
        globalMap.put("$is_IE", "boolean");

        globalMap.put("$is_apache", "boolean");
        globalMap.put("$is_IIS", "boolean");
        globalMap.put("$is_iis7", "boolean");

        globalMap.put("$wp_version", "string");
        globalMap.put("$wp_db_version", "int");
        globalMap.put("$tynymce_version", "string");
        globalMap.put("$manifest_version", "string");
        globalMap.put("$required_php_version", "string");
        globalMap.put("$required_mysql_version", "string");

        globalMap.put("$pagenow", "string");
        globalMap.put("$allowedposttags", "array");
        globalMap.put("$allowedtags", "array");

        Set<String> keySet = globalMap.keySet();
        for (String key : keySet) {
            String clazz = globalMap.get(key);
            PhpVariable phpVariable = new PhpVariable(key, new PhpClass(clazz, clazz));
            elements.add(phpVariable);
        }
        // $authordata
        PhpClass authorClass = new PhpClass("stdClass", "stdClass");
        authorClass.addField("$ID", "$ID");
        authorClass.addField("$user_login", "$user_login");
        authorClass.addField("$user_pass", "$user_pass");
        authorClass.addField("$user_nicename", "$user_nicename");
        authorClass.addField("$user_email", "$user_email");
        authorClass.addField("$user_url", "$user_url");
        authorClass.addField("$user_registered", "$user_registered");
        authorClass.addField("$user_activation_key", "$user_activation_key");
        authorClass.addField("$user_status", "$user_status");
        authorClass.addField("$display_name", "$display_name");
        elements.add(new PhpVariable("$authordata", authorClass));
    }

    @Override
    public List<PhpBaseElement> getElementsForCodeCompletion(FileObject fo) {
        PhpModule phpModule = PhpModule.forFileObject(fo);
        // check whether project is WordPress
        if (!WPUtils.isWP(phpModule)) {
            return new LinkedList<PhpBaseElement>();
        }
        return elements;
    }
}
