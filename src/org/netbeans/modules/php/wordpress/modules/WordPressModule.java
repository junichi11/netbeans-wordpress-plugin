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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class WordPressModule {

    public enum DIR_TYPE {

        ADMIN,
        CONTENT,
        ROOT,
        INCLUDES,
        PLUGINS,
        THEMES;
    }

    private final WordPressModuleImpl impl;
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
    public static final String PROPERTY_CHANGE_WP = "property-change-wp"; // NOI18N

    private WordPressModule(WordPressModuleImpl impl) {
        this.impl = impl;
    }

    public FileObject getPluginsDirectory() {
        return impl.getPluginsDirectory();
    }

    public FileObject getThemesDirectory() {
        return impl.getThemesDirectory();
    }

    public FileObject getIncludesDirectory() {
        return impl.getIncludesDirectory();
    }

    public FileObject getIncludesDirectory(String path) {
        return impl.getIncludesDirectory(path);
    }

    public FileObject getAdminDirectory() {
        return impl.getAdminDirectory();
    }

    public FileObject getWordPressRootDirecotry() {
        return impl.getWordPressRootDirecotry();
    }

    public FileObject getVersionFile() {
        return impl.getVersionFile();
    }

    public FileObject getDirecotry(DIR_TYPE dirType, String path) {
        return impl.getDirecotry(dirType, path);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void notifyPropertyChanged(PropertyChangeEvent event) {
        if (PROPERTY_CHANGE_WP.equals(event.getPropertyName())) {
            resetNode();
            refresh();
        }
    }

    void refresh() {
        impl.refresh();
    }

    void resetNode() {
        propertyChangeSupport.firePropertyChange(PROPERTY_CHANGE_WP, null, null);
    }

    //~ Inner class
    public static class Factory {

        private static final Map<PhpModule, WordPressModule> modules = new HashMap<PhpModule, WordPressModule>();

        public static WordPressModule forPhpModule(PhpModule phpModule) {
            WordPressModule module = modules.get(phpModule);
            if (module != null) {
                return module;
            }

            WordPressModuleImpl impl;
            if (phpModule == null) {
                impl = new WordPressDummyModuleImpl();
            } else {
                impl = new WordPress3ModuleImpl(phpModule);
            }
            module = new WordPressModule(impl);
            if (impl instanceof WordPress3ModuleImpl) {
                modules.put(phpModule, module);
            }
            return module;
        }

        public static void remove(PhpModule phpModule) {
            if (phpModule != null) {
                modules.remove(phpModule);
            }
        }
    }

}
