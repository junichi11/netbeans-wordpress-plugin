/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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
 */
package org.netbeans.modules.php.wordpress.ui.actions;

import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.ui.wizards.CreateMinimumBlankThemePanel;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle;

public class CreateMinimumBlankThemeAction extends BaseAction {

    private final List<String> themes = new ArrayList<>();
    private static final CreateMinimumBlankThemeAction INSTANCE = new CreateMinimumBlankThemeAction();
    private static final Logger LOGGER = Logger.getLogger(CreateMinimumBlankThemeAction.class.getName());

    private CreateMinimumBlankThemeAction() {
    }

    public static CreateMinimumBlankThemeAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @NbBundle.Messages("CreateMinimumBlankThemeAction.PureName=Minimum Theme")
    @Override
    protected String getPureName() {
        return Bundle.CreateMinimumBlankThemeAction_PureName();
    }

    @NbBundle.Messages({
        "CreateMinimumBlankThemeAction.dialog.title=Create Minimum Theme",
        "CreateMinimumBlankThemeAction.existing.directoryName=It already exists."
    })
    @Override
    protected void actionPerformed(PhpModule phpModule) {
        assert EventQueue.isDispatchThread();
        if (!WPUtils.isWP(phpModule)) {
            // called via shortcut
            return;
        }
        themes.clear();

        // get themes directory
        WordPressModule wpModule = WordPressModule.Factory.forPhpModule(phpModule);
        if (wpModule == null) {
            return;
        }
        final FileObject themesDirectory = wpModule.getThemesDirectory();
        if (themesDirectory == null) {
            return;
        }
        for (FileObject child : themesDirectory.getChildren()) {
            if (child.isFolder()) {
                themes.add(child.getNameExt());
            }
        }

        // create a panel & descriptor
        final CreateMinimumBlankThemePanel panel = new CreateMinimumBlankThemePanel();
        final DialogDescriptor dialogDescriptor = new DialogDescriptor(
                panel,
                Bundle.CreateMinimumBlankThemeAction_dialog_title(),
                true,
                DialogDescriptor.OK_CANCEL_OPTION,
                null,
                null
        );

        // add ChangeListener
        ChangeListener changeListener = (ChangeEvent e) -> {
            String themeDirectoryName = panel.getThemeDirectoryName();
            boolean existsDirectory = themes.contains(themeDirectoryName);
            dialogDescriptor.setValid(!existsDirectory);
            if (existsDirectory) {
                panel.setErrorMessage(Bundle.CreateMinimumBlankThemeAction_existing_directoryName());
            } else {
                panel.setErrorMessage(" "); // NOI18N
            }
        };
        panel.addChangeListener(changeListener);

        // show dialog
        Object result = DialogDisplayer.getDefault().notify(dialogDescriptor);
        if (result == NotifyDescriptor.OK_OPTION) {
            try {
                createMinimumTheme(themesDirectory, panel.getThemeDirectoryName());
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        panel.removeChangeListener(changeListener);
    }

    @NbBundle.Messages({
        "CreateMinimumBlankThemePanel.notFound.themeTemplate=Not found : style.css template file",
        "CreateMinimumBlankThemePanel.directoryName.error=Cannot create a directory"
    })
    private void createMinimumTheme(FileObject themesDirectory, String directoryName) throws IOException {
        // get template
        FileObject template = FileUtil.getConfigFile("Templates/WordPress/style.css"); // NOI18N
        if (template == null) {
            LOGGER.log(Level.WARNING, Bundle.CreateMinimumBlankThemePanel_notFound_themeTemplate());
            return;
        }

        // create a theme directory
        FileObject themeDirectory = themesDirectory.createFolder(directoryName);
        if (themeDirectory == null) {
            LOGGER.log(Level.WARNING, Bundle.CreateMinimumBlankThemePanel_directoryName_error());
            return;
        }

        // create an empty index.php
        themeDirectory.createData("index", "php"); // NOI18N

        // create a style.css
        DataObject templateDataObject = DataObject.find(template);
        DataFolder targetFolder = DataFolder.findFolder(themeDirectory);
        Map<String, String> parameters = new HashMap<>();
        // TODO add fields as well as child theme?
        DataObject styleCssDataObject = templateDataObject.createFromTemplate(targetFolder, "style.css", parameters); // NOI18N
        if (styleCssDataObject != null) {
            UiUtils.open(styleCssDataObject.getPrimaryFile(), 0);
        }
    }

}
