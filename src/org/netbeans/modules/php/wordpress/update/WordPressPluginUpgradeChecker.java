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
package org.netbeans.modules.php.wordpress.update;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.wordpress.WordPress;
import org.netbeans.modules.php.wordpress.commands.WordPressCli;
import org.netbeans.modules.php.wordpress.ui.options.WordPressOptions;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = WordPressUpgradeChecker.class)
public class WordPressPluginUpgradeChecker implements WordPressUpgradeChecker {

    private boolean hasUpgrade = false;
    private ArrayList<UpdateItem> items;

    @Override
    public boolean hasUpgrade(PhpModule phpModule) {
        WordPressOptions options = WordPressOptions.getInstance();
        if (StringUtils.isEmpty(options.getWpCliPath()) || !options.isCheckPluginNewVersion()) {
            return false;
        }
        for (UpdateItem item : getUpdateItems(phpModule)) {
            if (item.isUpdate()) {
                hasUpgrade = true;
                return hasUpgrade;
            }
        }
        hasUpgrade = false;
        return hasUpgrade;
    }

    @NbBundle.Messages({
        "# {0} - project",
        "WordPressPluginUpgradeChecker.notify.title=Notification({0}): New plugin version is available",
        "# {0} - status",
        "# {1} - project",
        "WordPressPluginUpgradeChecker.notify.detail={0} ({1})",})
    @Override
    public void notifyUpgrade(PhpModule phpModule) {
        if (!hasUpgrade) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (UpdateItem item : items) {
            if (!item.isUpdate()) {
                continue;
            }
            sb.append(String.format(" %s:%s", item.getName(), item.getVersion())); // NOI18N
        }
        NotificationDisplayer.getDefault().notify(
                Bundle.WordPressPluginUpgradeChecker_notify_title(phpModule.getDisplayName()),
                ImageUtilities.loadImageIcon(WordPress.WP_ICON_16, false),
                Bundle.WordPressPluginUpgradeChecker_notify_detail(sb.toString(), phpModule.getDisplayName()),
                new PluginUpdateActionListener(phpModule)
        );
    }

    private List<UpdateItem> getUpdateItems(PhpModule phpModule) {
        items = new ArrayList<>();
        try {
            // get result
            WordPressCli wpCli = WordPressCli.getDefault(false);
            List<String> lines = wpCli.getPluginStatus(phpModule);
            boolean isStart = false;
            for (String line : lines) {
                if (line.isEmpty()) {
                    break;
                }
                if (line.endsWith(":")) { // NOI18N
                    isStart = true;
                    continue;
                }
                if (!isStart) {
                    continue;
                }
                UpdateItem item = UpdateItem.Factory.create(line);
                if (item != null) {
                    items.add(item);
                }
            }
        } catch (InvalidPhpExecutableException ex) {
            Exceptions.printStackTrace(ex);
        }
        return items;
    }

    //~ Inner classes
    private static class PluginUpdateActionListener implements ActionListener {

        private final PhpModule phpModule;

        public PluginUpdateActionListener(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @NbBundle.Messages("PluginUpdateActionListener.comfirmation=Do you want to update? (run wp plugin update --all)")
        @Override
        public void actionPerformed(ActionEvent e) {
            if (StringUtils.isEmpty(WordPressOptions.getInstance().getWpCliPath())) {
                UpgradeUtils.showInvalidWpCliDialog();
                return;
            }

            // confirmation
            NotifyDescriptor.Confirmation comfirmation = new NotifyDescriptor.Confirmation(
                    Bundle.PluginUpdateActionListener_comfirmation(),
                    NotifyDescriptor.OK_CANCEL_OPTION
            );
            if (DialogDisplayer.getDefault().notify(comfirmation) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            try {
                // plugin update --all
                WordPressCli wpCli = WordPressCli.getDefault(true);
                wpCli.pluginUpdate(phpModule, Arrays.asList(WordPressCli.ALL_PARAM));
            } catch (InvalidPhpExecutableException ex) {
                Exceptions.printStackTrace(ex);
            }

        }
    }

}
