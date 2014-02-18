/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.wordpress.modules;

import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.wordpress.preferences.WordPressPreferences;
import static org.netbeans.modules.php.wordpress.util.WPFileUtils.WP_ADMIN;
import static org.netbeans.modules.php.wordpress.util.WPFileUtils.WP_INCLUDES;
import static org.netbeans.modules.php.wordpress.util.WPFileUtils.WP_PLUGINS;
import static org.netbeans.modules.php.wordpress.util.WPFileUtils.WP_THEMES;
import org.openide.filesystems.FileObject;

public class WordPress3ModuleImpl extends WordPressModuleImpl {

    private FileObject pluginsDirectory;
    private FileObject themesDirectory;
    private FileObject includesDirectory;
    private FileObject adminDirectory;
    private FileObject contentDirectory;
    private FileObject wordPressRootDirectory;
    private final PhpModule phpModule;

    public WordPress3ModuleImpl(@NonNull PhpModule phpModule) {
        this.phpModule = phpModule;
        constructDirectories();
    }

    @Override
    public FileObject getPluginsDirectory() {
        return pluginsDirectory;
    }

    @Override
    public FileObject getThemesDirectory() {
        return themesDirectory;
    }

    @Override
    public FileObject getIncludesDirectory() {
        return includesDirectory;
    }

    @Override
    public FileObject getIncludesDirectory(String path) {
        if (includesDirectory == null) {
            return null;
        }
        return includesDirectory.getFileObject(path);
    }

    @Override
    public FileObject getAdminDirectory() {
        return adminDirectory;
    }

    @Override
    public FileObject getContentDirectory() {
        return contentDirectory;
    }

    @Override
    public FileObject getWordPressRootDirecotry() {
        return wordPressRootDirectory;
    }

    @Override
    public FileObject getVersionFile() {
        return getIncludesDirectory("version.php"); // NOI18N
    }

    @Override
    public FileObject getDirecotry(WordPressModule.DIR_TYPE dirType) {
        return getDirecotry(dirType, null);
    }

    @Override
    public FileObject getDirecotry(@NonNull WordPressModule.DIR_TYPE dirType, String path) {
        FileObject targetDirectory;
        switch (dirType) {
            case ADMIN:
                targetDirectory = getAdminDirectory();
                break;
            case CONTENT:
                targetDirectory = getContentDirectory();
                break;
            case INCLUDES:
                targetDirectory = getIncludesDirectory();
                break;
            case PLUGINS:
                targetDirectory = getPluginsDirectory();
                break;
            case THEMES:
                targetDirectory = getThemesDirectory();
                break;
            case ROOT:
                targetDirectory = getWordPressRootDirecotry();
                break;
            default:
                return null;
        }

        if (path != null && targetDirectory != null) {
            return targetDirectory.getFileObject(path);
        }
        return targetDirectory;
    }

    @Override
    public void refresh() {
        constructDirectories();
    }

    private void constructDirectories() {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            return;
        }
        String contentName = WordPressPreferences.getCustomContentName(phpModule);
        wordPressRootDirectory = sourceDirectory;
        contentDirectory = wordPressRootDirectory.getFileObject(contentName);
        pluginsDirectory = wordPressRootDirectory.getFileObject(String.format(WP_PLUGINS, contentName));
        themesDirectory = wordPressRootDirectory.getFileObject(String.format(WP_THEMES, contentName));
        includesDirectory = wordPressRootDirectory.getFileObject(WP_INCLUDES);
        adminDirectory = wordPressRootDirectory.getFileObject(WP_ADMIN);
    }

}
