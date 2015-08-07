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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.util.Charset;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CreatePluginAction extends BaseAction {

    private static final long serialVersionUID = 4960061081316192725L;
    private static final String README = "readme"; // NOI18N
    private static final String WP_CONFIG_ROOT = "org-netbeans-modules-php-wordpress"; // NOI18N
    private static final String WP_PLUGIN_TEMPLATE_PATH = WP_CONFIG_ROOT + "/WpPlugin.php"; // NOI18N
    private static final String WP_PLUGIN_README_TEMPLATE_PATH = WP_CONFIG_ROOT + "/readme.txt"; // NOI18N
    private static final String NAME_PLACE = "${name}"; // NOI18N
    private Set<String> existingPluignNames;
    private FileObject pluginsDirectory;
    private static final CreatePluginAction INSTANCE = new CreatePluginAction();
    private static final Logger LOGGER = Logger.getLogger(CreatePluginAction.class.getName());

    private CreatePluginAction() {
    }

    public static CreatePluginAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @Override
    @NbBundle.Messages("LBL_CreatePluginAction=Create Plugin")
    protected String getPureName() {
        return Bundle.LBL_CreatePluginAction();
    }

    @Override
    @NbBundle.Messages("LBL_PluginName=Plugin (Directory) Name")
    protected void actionPerformed(PhpModule phpModule) {
        // called via shortcut
        if (!WPUtils.isWP(phpModule)) {
            return;
        }

        // get plugins directory
        pluginsDirectory = WordPressModule.Factory.forPhpModule(phpModule).getPluginsDirectory();
        if (pluginsDirectory == null) {
            return;
        }

        // for validation
        setExistingPluginNames();

        // create dialog
        InputLine descriptor = new InputLine(Bundle.LBL_PluginName(), Bundle.LBL_PluginName(),
                NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.PLAIN_MESSAGE);
        descriptor.setValid(false);
        if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION) {
            // get plugin name
            String name = descriptor.getInputText().trim();
            if (StringUtils.isEmpty(name)) {
                LOGGER.log(Level.WARNING, "Not found:{0}", name);
                return;
            }

            // create plugin directory
            FileObject pluginDirectory = createPluginDirectory(name);
            if (pluginDirectory == null) {
                LOGGER.log(Level.WARNING, "Not found:{0}", "plugin directory");
                return;
            }

            // add files pluginName.php & readme.txt
            try {
                addPluginFiles(pluginDirectory, name);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }

            // open file
            FileObject plugin = pluginDirectory.getFileObject(name + ".php"); // NOI18N
            if (plugin != null) {
                UiUtils.open(plugin, 0);
            }
        }
    }

    /**
     * Add plugin files. It is so simple two files. main plugin file and readme.
     *
     * @param pluginDirectory
     * @param name plugin name
     * @throws IOException
     */
    private void addPluginFiles(FileObject pluginDirectory, String name) throws IOException {
        createReadme(pluginDirectory);
        createPluginFile(pluginDirectory, name);
    }

    /**
     * Create plugin directory.
     *
     * @param name
     * @return
     */
    private FileObject createPluginDirectory(String name) {
        try {
            return pluginsDirectory.createFolder(name);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;

    }

    /**
     * Set existing plugin names.
     */
    private void setExistingPluginNames() {
        existingPluignNames = new HashSet<>();
        if (pluginsDirectory != null) {
            for (FileObject child : pluginsDirectory.getChildren()) {
                if (child.isFolder()) {
                    existingPluignNames.add(child.getName());
                }
            }
        }

    }

    /**
     * Get existing plugin names.
     *
     * @return
     */
    public Set<String> getExistingPluginNames() {
        if (existingPluignNames == null) {
            return Collections.emptySet();
        }
        return existingPluignNames;
    }

    /**
     * Create readme.txt.
     *
     * @param pluginDirectory
     * @throws IOException
     */
    private void createReadme(FileObject pluginDirectory) throws IOException {
        FileObject readmeTemplate = FileUtil.getConfigFile(WP_PLUGIN_README_TEMPLATE_PATH);
        if (readmeTemplate == null) {
            return;
        }
        FileUtil.copyFile(readmeTemplate, pluginDirectory, README);
    }

    /**
     * Create file for plugin. It is very simple file.
     *
     * @param pluginDirectory
     * @param name plugin name
     * @throws IOException
     */
    private void createPluginFile(FileObject pluginDirectory, String name) throws IOException {
        FileObject pluginTemplate = FileUtil.getConfigFile(WP_PLUGIN_TEMPLATE_PATH);
        if (pluginTemplate == null) {
            LOGGER.log(Level.WARNING, "Not found:{0}", "plugin template");
            return;
        }
        OutputStream outputPlugin = pluginDirectory.createAndOpen(name + ".php"); // NOI18N
        try {
            List<String> lines = pluginTemplate.asLines(Charset.UTF8);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputPlugin, Charset.UTF8));
            try {
                for (String line : lines) {
                    if (line.contains(NAME_PLACE)) {
                        line = line.replace(NAME_PLACE, name);
                    }
                    pw.println(line);
                }
            } finally {
                pw.close();
            }
        } finally {
            outputPlugin.close();
        }
    }

    //~ inner classes
    private class InputLine extends NotifyDescriptor.InputLine {

        public InputLine(String text, String title) {
            super(text, title);
            addDocumentListener();
        }

        public InputLine(String text, String title, int optionType, int messageType) {
            super(text, title, optionType, messageType);
            addDocumentListener();
        }

        private void addDocumentListener() {
            Document document = textField.getDocument();
            document.addDocumentListener(new CreatePluginAction.DocumentListenerImpl(this));
        }
    }

    private class DocumentListenerImpl implements DocumentListener {

        private final InputLine inputLine;
        private final Set<String> existingPlugins;

        public DocumentListenerImpl(InputLine inputLine) {
            this.inputLine = inputLine;
            existingPlugins = getExistingPluginNames();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            fire();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            fire();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            fire();
        }

        private void fire() {
            String input = inputLine.getInputText().trim();
            if (input.isEmpty() || existingPlugins.contains(input)) {
                inputLine.setValid(false);
            } else {
                inputLine.setValid(true);
            }
        }
    }
}
