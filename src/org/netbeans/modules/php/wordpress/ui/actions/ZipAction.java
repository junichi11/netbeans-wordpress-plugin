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

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.wordpress.util.WPFileUtils;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "File", id = "org.netbeans.modules.php.wordpress.ui.actions.ZipAction")
@ActionRegistration(displayName = "#CTL_ZipAction", lazy = false)
@ActionReference(path = "Loaders/folder/any/Actions", position = 1650)
@Messages("CTL_ZipAction=WordPress Zip compress")
public final class ZipAction extends AbstractAction implements ContextAwareAction {

    private static final long serialVersionUID = 1781415535743693501L;

    private DataObject context;
    private static final Logger LOGGER = Logger.getLogger(ZipAction.class.getName());

    public ZipAction() {
        this(null);
    }

    public ZipAction(DataObject context) {
        this.context = context;
        if (context == null) {
            putValue(Action.NAME, ""); // NOI18N
        } else {
            putValue(Action.NAME, Bundle.CTL_ZipAction()); // NOI18N
        }
        setEnabled(context != null);
    }

    @Messages({
        "# {0} - file name",
        "ZipAction.error.file.already.exist=Zip file ({0}) already exists."
    })
    @Override
    public void actionPerformed(ActionEvent ev) {
        if (!isValidDirectory(getFileObject())) {
            return;
        }

        // zip compress
        FileObject target = getFileObject();
        try {
            WPFileUtils.zip(target);
        } catch (IOException ex) {
            // #36
            LOGGER.log(Level.WARNING, ex.getMessage());
            showErrorDialog(Bundle.ZipAction_error_file_already_exist(target.getName() + ".zip")); // NOI18N
        }
    }

    /**
     * Get FileObject from DataObject
     *
     * @return
     */
    private FileObject getFileObject() {
        return context.getPrimaryFile();
    }

    /**
     * Check whether parent directory is plugins or themes
     *
     * @return true if parent directory name is "plugins" or "themes", otherwise
     * false.
     */
    private boolean isValidDirectory(FileObject target) {
        if (target == null) {
            return false;
        }
        FileObject parent = target.getParent();
        String name = parent.getNameExt();
        if (parent.isFolder()) {
            if (name.equals("plugins") || name.equals("themes")) { // NOI18N
                // #36
                String zipFileName = target.getName() + ".zip"; // NOI18N
                FileObject zipFile = parent.getFileObject(zipFileName);
                if (zipFile != null) {
                    showErrorDialog(Bundle.ZipAction_error_file_already_exist(zipFileName));
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Show error message dialog.
     *
     * @param errorMessage Error message
     */
    private void showErrorDialog(String errorMessage) {
        NotifyDescriptor.Message message = new NotifyDescriptor.Message(errorMessage, NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(message);
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        DataObject dataObject = actionContext.lookup(DataObject.class);
        if (dataObject == null) {
            return this;
        }
        FileObject fileObject = dataObject.getPrimaryFile();
        PhpModule phpModule = PhpModule.Factory.forFileObject(fileObject);
        if (phpModule == null) {
            return this;
        }
        if (!WPUtils.isWP(phpModule)) {
            return this;
        }
        if (!isValidDirectory(fileObject)) {
            return this;
        }
        return new ZipAction(dataObject);
    }
}
