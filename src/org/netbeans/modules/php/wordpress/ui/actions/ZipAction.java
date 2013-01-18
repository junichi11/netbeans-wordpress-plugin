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
import java.awt.event.ActionListener;
import java.io.IOException;
import org.netbeans.modules.php.wordpress.util.WPFileUtils;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "org.netbeans.modules.php.wordpress.ui.actions.ZipAction")
@ActionRegistration(
        displayName = "#CTL_ZipAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 2700),
    @ActionReference(path = "Loaders/folder/any/Actions", position = 1650)
})
@Messages("CTL_ZipAction=Zip compress")
public final class ZipAction implements ActionListener {

    private final DataObject context;

    public ZipAction(DataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (!isValidDirectory()) {
            return;
        }

        // zip compress
        FileObject target = getFileObject();
        try {
            WPFileUtils.zip(target);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
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
    private boolean isValidDirectory() {
        FileObject target = getFileObject();
        FileObject parent = target.getParent();
        String name = parent.getNameExt();
        if (parent.isFolder()) {
            if (name.equals("plugins") || name.equals("themes")) { // NOI18N
                return true;
            }
        }
        return false;
    }
}
