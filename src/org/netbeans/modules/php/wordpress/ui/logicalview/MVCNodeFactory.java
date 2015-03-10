/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2012 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.wordpress.ui.logicalview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;

/**
 *
 * @author junichi11
 */
@NodeFactory.Registration(projectType = "org-netbeans-modules-php-project", position = 500)
public class MVCNodeFactory implements NodeFactory {

    public MVCNodeFactory() {
    }

    @Override
    public NodeList<?> createNodes(Project p) {
        PhpModule phpModule = PhpModule.Factory.lookupPhpModule(p);
        return new MVCNodeList(phpModule);
    }

    private static class MVCNodeList implements NodeList<Node>, PropertyChangeListener {

        private final PhpModule phpModule;
        private static final Logger LOGGER = Logger.getLogger(MVCNodeList.class.getName());
        private final ChangeSupport changeSupport = new ChangeSupport(this);

        public MVCNodeList(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @Override
        public List<Node> keys() {
            if (WPUtils.isWP(phpModule)) {
                List<Node> list = new ArrayList<Node>();
                WordPressModule module = WordPressModule.Factory.forPhpModule(phpModule);
                // plugins
                FileObject pluginsDirectory = module.getPluginsDirectory();
                if (pluginsDirectory != null) {
                    addNode(list, pluginsDirectory);
                }
                // themes
                FileObject themesDirectory = module.getThemesDirectory();
                if (themesDirectory != null) {
                    addNode(list, themesDirectory);
                }
                return list;
            }
            return Collections.emptyList();
        }

        private void addNode(List<Node> list, FileObject fileObject) {
            if (fileObject != null) {
                DataFolder folder = getFolder(fileObject);
                if (folder != null) {
                    list.add(new MVCNode(folder, null, fileObject.getName()));
                }
            }
        }

        @Override
        public void addChangeListener(ChangeListener l) {
            changeSupport.addChangeListener(l);
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
            changeSupport.removeChangeListener(l);
        }

        @Override
        public Node node(Node node) {
            return node;
        }

        private DataFolder getFolder(FileObject fileObject) {
            if (fileObject != null && fileObject.isValid()) {
                try {
                    DataFolder dataFolder = DataFolder.findFolder(fileObject);
                    return dataFolder;
                } catch (Exception ex) {
                    LOGGER.log(Level.INFO, null, ex);
                }
            }
            return null;
        }

        @Override
        public void addNotify() {
            WordPressModule wpModule = WordPressModule.Factory.forPhpModule(phpModule);
            wpModule.addPropertyChangeListener(this);
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (WordPressModule.PROPERTY_CHANGE_WP.equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        fireChange();
                    }
                });
            }
        }

        void fireChange() {
            changeSupport.fireChange();
        }
    }
}
