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
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.netbeans.modules.php.api.framework.BadgeIcon;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.phpmodule.PhpModuleProperties;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.spi.editor.EditorExtender;
import org.netbeans.modules.php.spi.framework.PhpFrameworkProvider;
import org.netbeans.modules.php.spi.framework.PhpModuleActionsExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleExtender;
import org.netbeans.modules.php.spi.framework.PhpModuleIgnoredFilesExtender;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommandSupport;
import org.netbeans.modules.php.wordpress.commands.WordPressCommandSupport;
import org.netbeans.modules.php.wordpress.customizer.WordPressCustomizerExtender;
import org.netbeans.modules.php.wordpress.editor.WordPressEditorExtender;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.preferences.WordPressPreferences;
import org.netbeans.modules.php.wordpress.update.WordPressUpgradeChecker;
import org.netbeans.modules.php.wordpress.validators.WordPressModuleValidator;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public class WordPressPhpProvider extends PhpFrameworkProvider {

    private static final RequestProcessor RP = new RequestProcessor(WordPressPhpProvider.class);
    private static final WordPressPhpProvider INSTANCE = new WordPressPhpProvider();
    private static final String ICON_PATH = "org/netbeans/modules/php/wordpress/resources/wordpress_badge_8.png"; // NOI18N
    private final BadgeIcon badgeIcon;
    public static final Set<String> WP_DIRS = new HashSet<String>();

    static {
        WP_DIRS.add("wp-admin"); // NOI18N
        WP_DIRS.add("wp-includes"); // NOI18N
    }

    @PhpFrameworkProvider.Registration(position = 1000)
    public static WordPressPhpProvider getInstance() {
        return INSTANCE;
    }

    @Override
    public BadgeIcon getBadgeIcon() {
        return badgeIcon;
    }

    @NbBundle.Messages({
        "LBL_CMS_Name=WordPress PHP Web Blog/CMS",
        "LBL_CMS_Description=WordPress PHP Web Blog/CMS"
    })
    private WordPressPhpProvider() {
        super(Bundle.LBL_CMS_Name(), Bundle.LBL_CMS_Name(), Bundle.LBL_CMS_Description());
        badgeIcon = new BadgeIcon(
                ImageUtilities.loadImage(ICON_PATH),
                WordPressPhpProvider.class.getResource("/" + ICON_PATH)); // NOI18N

    }

    @Override
    public boolean isInPhpModule(PhpModule pm) {
        return WordPressPreferences.isEnabled(pm);
    }

    @Override
    public File[] getConfigurationFiles(PhpModule pm) {
        List<File> files = new LinkedList<File>();
        FileObject sourceDirectory = pm.getSourceDirectory();
        if (sourceDirectory != null) {
            FileObject config = sourceDirectory.getFileObject("wp-config.php"); // NOI18N
            if (config != null) {
                files.add(FileUtil.toFile(config));
            }
        }

        return files.toArray(new File[files.size()]);
    }

    @Override
    public PhpModuleExtender createPhpModuleExtender(PhpModule pm) {
        return new WordPressPhpModuleExtender();
    }

    @Override
    public PhpModuleProperties getPhpModuleProperties(PhpModule pm) {
        PhpModuleProperties properties = new PhpModuleProperties();

        return properties;
    }

    @Override
    public PhpModuleActionsExtender getActionsExtender(PhpModule pm) {
        return new WordPressActionsExtender();
    }

    @Override
    public PhpModuleIgnoredFilesExtender getIgnoredFilesExtender(PhpModule pm) {
        return null;
    }

    @Override
    public FrameworkCommandSupport getFrameworkCommandSupport(PhpModule pm) {
        return new WordPressCommandSupport(pm);
    }

    @Override
    public PhpModuleCustomizerExtender createPhpModuleCustomizerExtender(PhpModule phpModule) {
        return new WordPressCustomizerExtender(phpModule);
    }

    @Override
    public EditorExtender getEditorExtender(PhpModule pm) {
        return new WordPressEditorExtender();
    }

    @Override
    public void phpModuleClosed(PhpModule phpModule) {
        WordPressModule.Factory.remove(phpModule);
    }

    @NbBundle.Messages({
        "# {0} - name",
        "WordPressPhpProvider.autoditection=WordPress autoditection : {0}",
        "WordPressPhpProvider.autoditection.action=If you want to enable as WordPress project, please click here."
    })
    @Override
    public void phpModuleOpened(PhpModule phpModule) {
        // this method is run for not only WordPress projects but also all php projects,
        // so need check i.e. avoid NPE
        if (isInPhpModule(phpModule)) {
            // check new version
            Collection<? extends WordPressUpgradeChecker> checkers = Lookup.getDefault().lookupAll(WordPressUpgradeChecker.class);
            for (WordPressUpgradeChecker checker : checkers) {
                if (checker.hasUpgrade(phpModule)) {
                    checker.notifyUpgrade(phpModule);
                }
            }
        }
        RP.schedule(new WordPressAutoDetectionTask(phpModule), 1, TimeUnit.MINUTES);
    }

    //~ Inner class
    private static class WordPressAutoDetectionTask implements Runnable {

        private final PhpModule phpModule;
        private Notification notification;

        public WordPressAutoDetectionTask(PhpModule phpModule) {
            this.phpModule = phpModule;
        }

        @Override
        public void run() {
            // auto detection
            FileObject wordPressRoot = phpModule.getSourceDirectory();
            if (wordPressRoot == null) {
                return;
            }
            ValidationResult result = new WordPressModuleValidator()
                    .validateWordPressDirectories(wordPressRoot, WordPressPreferences.getCustomContentName(phpModule))
                    .getResult();
            if (result.hasWarnings()) {
                return;
            }

            // show notification displayer
            if (!WordPressPreferences.isEnabled(phpModule)) {
                NotificationDisplayer notificationDisplayer = NotificationDisplayer.getDefault();
                notification = notificationDisplayer.notify(
                        Bundle.WordPressPhpProvider_autoditection(phpModule.getDisplayName()), // title
                        NotificationDisplayer.Priority.LOW.getIcon(), // icon
                        Bundle.WordPressPhpProvider_autoditection_action(),
                        new WordPressAutoDetectionActionListener(), // action
                        NotificationDisplayer.Priority.LOW); // priority
            }

        }

        private class WordPressAutoDetectionActionListener implements ActionListener {

            public WordPressAutoDetectionActionListener() {
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                WordPressPreferences.setEnabled(phpModule, true);
                WordPressModule module = WordPressModule.Factory.forPhpModule(phpModule);
                module.notifyPropertyChanged(new PropertyChangeEvent(this, WordPressModule.PROPERTY_CHANGE_WP, null, null));
                phpModule.notifyPropertyChanged(new PropertyChangeEvent(this, PhpModule.PROPERTY_FRAMEWORKS, null, null));
                notification.clear();
            }
        }
    }

}
