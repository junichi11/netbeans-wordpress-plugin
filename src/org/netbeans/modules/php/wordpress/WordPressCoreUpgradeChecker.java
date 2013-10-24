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
package org.netbeans.modules.php.wordpress;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.wordpress.wpapis.WordPressVersionCheckApi;
import org.netbeans.modules.php.wordpress.commands.WordPressCli;
import org.netbeans.modules.php.wordpress.ui.options.WordPressOptions;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = WordPressUpgradeChecker.class)
public final class WordPressCoreUpgradeChecker implements WordPressUpgradeChecker {

    private String upgradeVersionNumber;
    private static final Logger LOGGER = Logger.getLogger(WordPressCoreUpgradeChecker.class.getName());

    public WordPressCoreUpgradeChecker() {
    }

    @Override
    public boolean hasUpgrade(PhpModule phpModule) {
        try {
            if (StringUtils.isEmpty(upgradeVersionNumber)) {
                WordPressVersionCheckApi versionCheckApi = new WordPressVersionCheckApi();
                versionCheckApi.parse();
                upgradeVersionNumber = versionCheckApi.getVersion();
                if (StringUtils.isEmpty(upgradeVersionNumber)) {
                    // XXX throw exception?
                    return false;
                }
            }

            WordPressVersion version = WordPressVersion.create(phpModule);
            return hasUpgrade(version);
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.WARNING, ex.getLocalizedMessage());
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Isn't connected to Internet:{0}", ex.getLocalizedMessage());
        }

        return false;
    }

    /**
     * Compare with current version number.
     *
     * @param version current version
     * @return true new version available, false otherwise
     * @throws NumberFormatException
     */
    private boolean hasUpgrade(WordPressVersion version) throws NumberFormatException {
        // compare version
        String[] splitVersion = upgradeVersionNumber.split("[.]"); // NOI18N
        int length = splitVersion.length;
        if (length >= 2) {
            int majorVersion = Integer.parseInt(splitVersion[0]);
            int minorVersion = Integer.parseInt(splitVersion[1]);
            if (version.getMajor() < majorVersion) {
                return true;
            } else if (version.isMajor(majorVersion)) {
                if (version.getMinor() < minorVersion) {
                    return true;
                }
            }
        }
        if (length >= 3) {
            int revisionVersion = Integer.parseInt(splitVersion[2]);
            if (version.getRevision() < revisionVersion) {
                return true;
            }
        }
        return false;
    }

    @NbBundle.Messages({
        "WordPressUpgradeChecker.core.notify.title=Notification: New version is available",
        "# {0} - version number",
        "# {1} - display name",
        "WordPressUpgradeChecker.core.notify.detail=New version is available: {0} ({1})",})
    @Override
    public void notifyUpgrade(PhpModule phpModule) {
        if (StringUtils.isEmpty(upgradeVersionNumber)) {
            return;
        }
        NotificationDisplayer.getDefault().notify(
                Bundle.WordPressUpgradeChecker_core_notify_title(),
                ImageUtilities.loadImageIcon(WordPress.WP_ICON_16, false),
                Bundle.WordPressUpgradeChecker_core_notify_detail(upgradeVersionNumber, phpModule.getDisplayName()),
                new CoreUpdateActionListener(phpModule)
        );
    }

    public String getUpgradeVersionNumber() {
        return upgradeVersionNumber;
    }

    //~ Inner classes
    private static class CoreUpdateActionListener implements ActionListener {

        private final PhpModule phpModule;

        public CoreUpdateActionListener(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @NbBundle.Messages("CoreUpdateActionListener.comfirmation=Do you want to update? (run wp core update)")
        @Override
        public void actionPerformed(ActionEvent e) {
            if (StringUtils.isEmpty(WordPressOptions.getInstance().getWpCliPath())) {
                return;
            }

            // confirmation
            NotifyDescriptor.Confirmation comfirmation = new NotifyDescriptor.Confirmation(
                    Bundle.CoreUpdateActionListener_comfirmation(),
                    NotifyDescriptor.OK_CANCEL_OPTION
            );
            if (DialogDisplayer.getDefault().notify(comfirmation) != NotifyDescriptor.OK_OPTION) {
                return;
            }
            try {
                WordPressCli wpCli = WordPressCli.getDefault(true);
                wpCli.coreUpdate(phpModule, Collections.<String>emptyList());
            } catch (InvalidPhpExecutableException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
