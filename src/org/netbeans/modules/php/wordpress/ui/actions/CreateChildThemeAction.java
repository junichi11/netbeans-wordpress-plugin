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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.ui.wizards.CreateChildThemePanel;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.netbeans.modules.php.wordpress.validators.WordPressDirectoryNameValidator;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class CreateChildThemeAction extends BaseAction implements ChangeListener {

    private CreateChildThemePanel panel;
    private DialogDescriptor descriptor;
    private final List<String> themes = new ArrayList<String>();
    private static final CreateChildThemeAction INSTANCE = new CreateChildThemeAction();
    private static final Logger LOGGER = Logger.getLogger(CreateChildThemeAction.class.getName());
    private static final long serialVersionUID = -2231810352652363996L;

    private CreateChildThemeAction() {
    }

    public static CreateChildThemeAction getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @Override
    @NbBundle.Messages("LBL_CreateChildThemeBaseAction=Create Child Theme")
    protected String getPureName() {
        return Bundle.LBL_CreateChildThemeBaseAction();
    }

    @Override
    @NbBundle.Messages({
        "CreateChildThemeAction.notFound.template=Not found : style.css template file for child theme",
        "CreateChildThemeAction.childFolder.error=Can't create a child theme folder"
    })
    protected void actionPerformed(PhpModule phpModule) {
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

        if (themes.isEmpty()) {
            return;
        }

        Collections.sort(themes);

        // show dialog
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                getDescriptor().setValid(false);
                DialogDisplayer displayer = DialogDisplayer.getDefault();
                if (displayer.notify(getDescriptor()) == DialogDescriptor.OK_OPTION) {
                    try {
                        createChildTheme(themesDirectory);
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
                clear();
            }
        });
    }

    private void createChildTheme(FileObject themesDirectory) throws IOException, DataObjectNotFoundException {
        // get template
        FileObject template = FileUtil.getConfigFile("Templates/WordPress/child-style.css"); // NOI18N
        if (template == null) {
            LOGGER.log(Level.WARNING, Bundle.CreateChildThemeAction_notFound_template());
            return;
        }
        // create child theme directory and style.css
        FileObject childThemeDirectory = themesDirectory.createFolder(getPanel().getChildThemeName());
        if (childThemeDirectory == null) {
            LOGGER.log(Level.WARNING, Bundle.CreateChildThemeAction_childFolder_error());
            return;
        }
        DataObject templateDataObject = DataObject.find(template);
        DataFolder targetFolder = DataFolder.findFolder(childThemeDirectory);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("name", getPanel().getChildThemeName()); // NOI18N
        parameters.put("parent", getPanel().getParentThemeName()); // NOI18N
        parameters.put("uri", getPanel().getChildThemeUri()); // NOI18N
        parameters.put("description", getPanel().getDescription()); // NOI18N
        parameters.put("version", getPanel().getVersion()); // NOI18N
        parameters.put("author", getPanel().getAuthor()); // NOI18N
        parameters.put("authorUri", getPanel().getAuthorUri()); // NOI18N
        parameters.put("tags", getPanel().getTags()); // NOI18N
        parameters.put("textDomain", getPanel().getTextDomain()); // NOI18N
        DataObject styleCssDataObject = templateDataObject.createFromTemplate(targetFolder, "style.css", parameters); // NOI18N
        if (styleCssDataObject != null) {
            UiUtils.open(styleCssDataObject.getPrimaryFile(), 0);
        }
    }

    private void clear() {
        if (panel != null) {
            panel.removeChangeListener(this);
        }
        panel = null;
        descriptor = null;
        themes.clear();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (descriptor == null || panel == null) {
            return;
        }
        String childThemeName = getPanel().getChildThemeName();
        WordPressDirectoryNameValidator validator = new WordPressDirectoryNameValidator();
        ValidationResult result = validator.validateName(childThemeName)
                .validateExistingName(childThemeName, themes)
                .getResult();

        // errors
        List<ValidationResult.Message> errors = result.getErrors();
        if (!errors.isEmpty()) {
            getDescriptor().setValid(false);
            getPanel().setError(result.getErrors().get(0).getMessage());
            return;
        }

        // warnings
        List<ValidationResult.Message> warnings = result.getWarnings();
        if (!warnings.isEmpty()) {
            getDescriptor().setValid(false);
            getPanel().setError(result.getWarnings().get(0).getMessage());
            return;
        }

        // everything ok
        getDescriptor().setValid(true);
        getPanel().setError(" "); // NOI18N
    }

    private CreateChildThemePanel getPanel() {
        if (panel == null) {
            panel = new CreateChildThemePanel(themes);
            panel.addChangeListener(this);
        }
        return panel;
    }

    private DialogDescriptor getDescriptor() {
        if (descriptor == null) {
            descriptor = new DialogDescriptor(getPanel(), Bundle.LBL_CreateChildThemeBaseAction(), true, DialogDescriptor.OK_CANCEL_OPTION, null, null);
        }
        return descriptor;
    }

}
