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

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.FileSensitiveActions;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
import org.openide.actions.FileSystemAction;
import org.openide.actions.FindAction;
import org.openide.actions.PasteAction;
import org.openide.actions.ToolsAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFilter;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.ProxyLookup;

/**
 * use org.netbeans/modules/php/project/ui/logicalview/SrcNode
 *
 * @author Radek Matous, junichi11
 * @see org.netbeans/modules/php/project/ui/logicalview/SrcNode
 */
public class MVCNode extends FilterNode {

    private static final String ICON_PATH = "org/netbeans/modules/php/wordpress/resources/wordpress_icon_8.png"; //NOI18N
    private static final Image WP_ICON = ImageUtilities.loadImage(ICON_PATH);

    /**
     * creates source root node based on specified DataFolder. Uses specified
     * name.
     */
    MVCNode(DataFolder folder, DataFilter filter, String name) {
        this(folder, new FilterNode(folder.getNodeDelegate(), folder.createNodeChildren(filter)), name);
    }

    private MVCNode(DataFolder folder, FilterNode node, String name) {
        super(node, new MVCNode.FolderChildren(node, false), new ProxyLookup(folder.getNodeDelegate().getLookup()));

        disableDelegation(DELEGATE_GET_DISPLAY_NAME | DELEGATE_SET_DISPLAY_NAME | DELEGATE_GET_SHORT_DESCRIPTION | DELEGATE_GET_ACTIONS);
        setDisplayName(name);
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.mergeImages(super.getIcon(type), WP_ICON, 7, 7);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return ImageUtilities.mergeImages(super.getOpenedIcon(type), WP_ICON, 7, 7);
    }

    @Override
    public boolean canCopy() {
        return false;
    }

    @Override
    public boolean canCut() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    @Override
    @NbBundle.Messages({
        "LBL_DownloadCommand=Download...",
        "LBL_UploadCommand=Upload...",
        "LBL_SyncCommand=Synchronize..."
    })
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        actions.add(CommonProjectActions.newFileAction());
        actions.add(null);
        actions.add(FileSensitiveActions.fileCommandAction("dowonload", Bundle.LBL_DownloadCommand(), null));
        actions.add(FileSensitiveActions.fileCommandAction("upload", Bundle.LBL_UploadCommand(), null));
        actions.add(FileSensitiveActions.fileCommandAction("synchronize", Bundle.LBL_SyncCommand(), null));
        actions.add(null);
        actions.add(SystemAction.get(FileSystemAction.class));
        actions.add(null);
        actions.add(SystemAction.get(FindAction.class));
        actions.add(null);
        actions.add(SystemAction.get(PasteAction.class));
        actions.add(null);
        actions.add(SystemAction.get(ToolsAction.class));
        actions.add(null);
        // customizer - open sources for source node, phpunit for test node
        Action customizeAction = null;
        customizeAction = CommonProjectActions.customizeProjectAction();
        actions.add(customizeAction);
        return actions.toArray(new Action[actions.size()]);
    }
    static final Action[] COMMON_ACTIONS = new Action[]{
        null,
        FileSensitiveActions.fileCommandAction("dowonload", Bundle.LBL_DownloadCommand(), null),
        FileSensitiveActions.fileCommandAction("upload", Bundle.LBL_UploadCommand(), null),
        FileSensitiveActions.fileCommandAction("synchronize", Bundle.LBL_SyncCommand(), null),};

    public static Action createDownloadAction() {
        return COMMON_ACTIONS[1];
    }

    public static Action createUploadAction() {
        return COMMON_ACTIONS[2];
    }

    public static Action createSynchronizeAction() {
        return COMMON_ACTIONS[3];
    }

    /**
     * Children for node that represents folder (SrcNode or PackageNode)
     */
    private static class FolderChildren extends FilterNode.Children {
        // common actions for both PackageNode and ObjectNode (equals has to be the same)

        private final boolean isTest;

        FolderChildren(final Node originalNode, boolean isTest) {
            super(originalNode);
            this.isTest = isTest;
        }

        @Override
        protected Node[] createNodes(Node key) {
            return super.createNodes(key);
        }

        @Override
        protected Node copyNode(final Node originalNode) {
            FileObject fo = originalNode.getLookup().lookup(FileObject.class);
            if (fo == null) {
                // #201301 - what to do now?
                Logger.getLogger(MVCNode.FolderChildren.class.getName()).log(Level.WARNING, "No fileobject found for node: {0}", originalNode);
                return super.copyNode(originalNode);
            }
            if (fo.isFolder()) {
                return new MVCNode.PackageNode(originalNode, isTest);
            }
            return new MVCNode.ObjectNode(originalNode, isTest);
        }
    }

    private static final class PackageNode extends FilterNode {

        public PackageNode(final Node originalNode, boolean isTest) {
            super(originalNode, new MVCNode.FolderChildren(originalNode, isTest),
                    new ProxyLookup(originalNode.getLookup()));

        }

        @Override
        public Action[] getActions(boolean context) {
            List<Action> actions = new ArrayList<Action>();
            actions.addAll(Arrays.asList(getOriginal().getActions(context)));
            Action[] commonActions = getCommonActions();
            int idx = actions.indexOf(SystemAction.get(PasteAction.class));
            for (int i = 0; i < commonActions.length; i++) {
                if (idx >= 0 && idx + commonActions.length < actions.size()) {
                    //put on the proper place after paste
                    actions.add(idx + i + 1, commonActions[i]);
                } else {
                    //else put at the tail
                    actions.add(commonActions[i]);
                }
            }
            return actions.toArray(new Action[actions.size()]);
        }

        @Override
        public Image getIcon(int type) {
            return super.getIcon(type);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return super.getOpenedIcon(type);
        }

        private Action[] getCommonActions() {
            // remove sync action
            Action[] actions = new Action[COMMON_ACTIONS.length - 1];
            System.arraycopy(COMMON_ACTIONS, 0, actions, 0, COMMON_ACTIONS.length - 1);
            return actions;
        }
    }

    private static final class ObjectNode extends FilterNode {

        private final Node originalNode;
        private final boolean isTest;

        public ObjectNode(final Node originalNode, boolean isTest) {
            super(originalNode);
            this.originalNode = originalNode;
            this.isTest = isTest;
        }

        @Override
        public Action[] getActions(boolean context) {
            List<Action> actions = new ArrayList<Action>();
            actions.addAll(Arrays.asList(getOriginal().getActions(context)));
            int idx = actions.indexOf(SystemAction.get(PasteAction.class));
            Action[] toAdd = getCommonActions();
            for (int i = 0; i < toAdd.length; i++) {
                if (idx >= 0 && idx + toAdd.length < actions.size()) {
                    //put on the proper place after rename
                    actions.add(idx + i + 1, toAdd[i]);
                } else {
                    //else put at the tail
                    actions.add(toAdd[i]);
                }
            }
            //#143782 find usages on php file has no sense
            for (Iterator<Action> it = actions.iterator(); it.hasNext();) {
                Action action = it.next();
                //hard code string WhereUsedAction chosen not need to depend on refactoring
                //just for this minority issue
                if (action != null
                        && action.getClass().getName().indexOf("WhereUsedAction") != -1) { // NOI18N
                    it.remove();
                    break;
                }
            }
            return actions.toArray(new Action[actions.size()]);
        }

        @NbBundle.Messages({
            "LBL_RunProject=Run",
            "LBL_DebugProject=Debug",
            "LBL_TestFile=Test"
        })
        private Action[] getCommonActions() {
            List<Action> toAdd = new ArrayList<Action>();
            if (isPhpOrHtmlFile(getFileObject())) {
                // not available for multiple selected nodes => create new instance every time
                toAdd.add(null);
                toAdd.add(ProjectSensitiveActions.projectCommandAction("run.single", Bundle.LBL_RunProject(), null));
                toAdd.add(ProjectSensitiveActions.projectCommandAction("debug.single", Bundle.LBL_DebugProject(), null));
                if (!isTest) {
                    toAdd.add(ProjectSensitiveActions.projectCommandAction("test.single", Bundle.LBL_TestFile(), null));
                }
            }

            List<Action> actions = new ArrayList<Action>(COMMON_ACTIONS.length + toAdd.size());
            actions.addAll(toAdd);
            if (!isTest) {
                actions.addAll(Arrays.asList(COMMON_ACTIONS));
            }

            return actions.toArray(new Action[actions.size()]);
        }

        public static boolean isPhpOrHtmlFile(FileObject file) {
            assert file != null;
            String mimeType = FileUtil.getMIMEType(file, FileUtils.PHP_MIME_TYPE, "text/html");
            return FileUtils.PHP_MIME_TYPE.equals(mimeType) || "text/html".equals(mimeType);
        }

        private FileObject getFileObject() {
            FileObject fileObject = originalNode.getLookup().lookup(FileObject.class);
            if (fileObject != null) {
                return fileObject;
            }
            // just fallback, should not happen
            DataObject dataObject = originalNode.getLookup().lookup(DataObject.class);
            assert dataObject != null;
            fileObject = dataObject.getPrimaryFile();
            assert fileObject != null;
            return fileObject;
        }
    }
}
