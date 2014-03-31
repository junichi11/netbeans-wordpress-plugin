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
package org.netbeans.modules.php.wordpress.ui.actions;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.ui.wizards.CreatePermalinkHtaccessPanel;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.openide.DialogDescriptor;
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
public class CreatePermalinkHtaccessAction extends BaseAction {

    private static final long serialVersionUID = -9023009833256287021L;
    private static final CreatePermalinkHtaccessAction INSTANCE = new CreatePermalinkHtaccessAction();
    private static final Logger LOGGER = Logger.getLogger(CreatePermalinkHtaccessAction.class.getName());

    public static CreatePermalinkHtaccessAction getInstance() {
        return INSTANCE;
    }

    private CreatePermalinkHtaccessAction() {
    }

    @NbBundle.Messages("WordPressAction.name=WordPress: ")
    @Override
    protected String getFullName() {
        return Bundle.WordPressAction_name() + getPureName();
    }

    @NbBundle.Messages("CreatePermalinkHtaccessAction.name=Create .htaccess for permalink")
    @Override
    protected String getPureName() {
        return Bundle.CreatePermalinkHtaccessAction_name();
    }

    @NbBundle.Messages({
        "CreatePermalinkHtaccessAction.panel.message=<html>.htaccess file already exists. <br>Do you really want to overwrite it?",
        "# {0} - description",
        "CreatePermalinkHtaccessAction.error=Create permalink .htaccess action: {0}",
        "CreatePermalinkHtaccessAction.error.wproot=Not fond WordPress root",
        "CreatePermalinkHtaccessAction.error.template=Not fond template",
        "CreatePermalinkHtaccessAction.error.template.text=Can't get template text",
        "CreatePermalinkHtaccessAction.error.copy=Can't copy tempate file"})
    @Override
    protected void actionPerformed(PhpModule pm) {
        // called via shortcut
        if (!WPUtils.isWP(pm)) {
            return;
        }

        WordPressModule wpModule = WordPressModule.Factory.forPhpModule(pm);
        FileObject wordPressRootDirecotry = wpModule.getWordPressRootDirecotry();
        if (wordPressRootDirecotry == null) {
            LOGGER.log(Level.WARNING, Bundle.CreatePermalinkHtaccessAction_error(Bundle.CreatePermalinkHtaccessAction_error_wproot()));
            return;
        }
        FileObject template = FileUtil.getConfigFile("Templates/WordPress/.htaccess"); // NOI18N
        if (template == null) {
            LOGGER.log(Level.WARNING, Bundle.CreatePermalinkHtaccessAction_error(Bundle.CreatePermalinkHtaccessAction_error_template()));
            return;
        }
        String contents = null;
        try {
            contents = template.asText("UTF-8"); // NOI18N
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (contents == null || contents.isEmpty()) {
            LOGGER.log(Level.WARNING, Bundle.CreatePermalinkHtaccessAction_error(Bundle.CreatePermalinkHtaccessAction_error_template_text()));
            return;
        }

        // check whether file already exits
        FileObject htaccessFile = wordPressRootDirecotry.getFileObject(".htaccess");
        if (htaccessFile != null) {
            CreatePermalinkHtaccessPanel panel = new CreatePermalinkHtaccessPanel()
                    .setMessage(Bundle.CreatePermalinkHtaccessAction_panel_message())
                    .setContents(contents);
            DialogDescriptor dialogDescriptor = new DialogDescriptor(panel, "Confirmation", true, DialogDescriptor.YES_NO_OPTION, null, null);
            if (DialogDisplayer.getDefault().notify(dialogDescriptor) != NotifyDescriptor.YES_OPTION) {
                return;
            }
        }
        try {
            if (htaccessFile != null) {
                htaccessFile.delete();
            }
            template.copy(wordPressRootDirecotry, template.getName(), template.getExt());
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, Bundle.CreatePermalinkHtaccessAction_error(Bundle.CreatePermalinkHtaccessAction_error_copy()));
        }
    }
}
