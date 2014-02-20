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
package org.netbeans.modules.php.wordpress.customizer;

import java.beans.PropertyChangeEvent;
import java.util.EnumSet;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.spi.framework.PhpModuleCustomizerExtender;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.preferences.WordPressPreferences;
import org.netbeans.modules.php.wordpress.validators.WordPressCustomizerValidator;
import org.netbeans.modules.php.wordpress.validators.WordPressModuleValidator;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class WordPressCustomizerExtender extends PhpModuleCustomizerExtender {

    private WordPressCustomizerExtenderPanel panel;
    private final PhpModule phpModule;
    private boolean originalEnabled;
    private boolean isValid;
    private String originalCustomeContentName;
    private String errorMessage;
    private String originalWordPressRoot;
    private String originalPlugins;
    private String originalThemes;

    public WordPressCustomizerExtender(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    @NbBundle.Messages("WordPressCustomizerExtender.displayname=WordPress")
    @Override
    public String getDisplayName() {
        return Bundle.WordPressCustomizerExtender_displayname();
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
        getPanel().addChangeListener(cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        getPanel().removeChangeListener(cl);
    }

    @Override
    public JComponent getComponent() {
        return getPanel();
    }

    @Override
    public HelpCtx getHelp() {
        return null;
    }

    @Override
    public boolean isValid() {
        validate();
        return isValid;
    }

    @Override
    public String getErrorMessage() {
        validate();
        return errorMessage;
    }

    private void validate() {
        if (!getPanel().isPluginEnabled()) {
            errorMessage = null;
            isValid = true;
            return;
        }
        String wordPressRootDirectoryPath = getPanel().getWordPressRootDirectory();
        String contentName = getPanel().getCustomContentName();
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        FileObject wordPressRoot = null;
        if (sourceDirectory != null) {
            wordPressRoot = sourceDirectory.getFileObject(wordPressRootDirectoryPath);
        }

        ValidationResult result = new WordPressCustomizerValidator()
                .validateWpContent(phpModule, wordPressRoot, contentName)
                .validateWordPressRootDirectory(phpModule, getPanel().getWordPressRootDirectory())
                .validatePluginsDirectory(phpModule, getPanel().getPluginsDirectory())
                .validateThemesDirectory(phpModule, getPanel().getThemesDirectory())
                .getResult();

        if (wordPressRoot != null) {
            ValidationResult wpResult = new WordPressModuleValidator()
                    .validateWordPressDirectories(wordPressRoot, contentName)
                    .getResult();
            result.merge(wpResult);
        }

        // error
        if (result.hasErrors()) {
            isValid = false;
            errorMessage = result.getErrors().get(0).getMessage();
            return;
        }

        // warning
        if (result.hasWarnings()) {
            isValid = false;
            errorMessage = result.getWarnings().get(0).getMessage();
            return;
        }

        // everything ok
        errorMessage = null;
        isValid = true;
    }

    @Override
    public EnumSet<Change> save(PhpModule pm) {
        boolean isEnabled = getPanel().isPluginEnabled();
        if (originalEnabled != isEnabled) {
            WordPressPreferences.setEnabled(phpModule, isEnabled);
            WordPressModule wpModule = WordPressModule.Factory.forPhpModule(phpModule);
            wpModule.notifyPropertyChanged(new PropertyChangeEvent(this, WordPressModule.PROPERTY_CHANGE_WP, null, null));
        }

        String customContentName = getPanel().getCustomContentName();
        if (!StringUtils.isEmpty(customContentName) && !originalCustomeContentName.equals(customContentName)) {
            WordPressPreferences.setCustomContentName(phpModule, customContentName);
        }

        String wordPressRoot = getPanel().getWordPressRootDirectory();
        if (!StringUtils.isEmpty(wordPressRoot) && !originalCustomeContentName.equals(wordPressRoot)) {
            WordPressPreferences.setWordPressRootPath(phpModule, wordPressRoot);
        }

        String plugins = getPanel().getPluginsDirectory();
        if (!StringUtils.isEmpty(plugins) && !originalCustomeContentName.equals(plugins)) {
            WordPressPreferences.setPluginsPath(phpModule, plugins);
        }

        String themes = getPanel().getThemesDirectory();
        if (!StringUtils.isEmpty(themes) && !originalCustomeContentName.equals(themes)) {
            WordPressPreferences.setThemesPath(phpModule, themes);
        }

        return EnumSet.of(Change.FRAMEWORK_CHANGE);
    }

    private WordPressCustomizerExtenderPanel getPanel() {
        if (panel == null) {
            panel = new WordPressCustomizerExtenderPanel();
            originalEnabled = WordPressPreferences.isEnabled(phpModule);
            originalCustomeContentName = WordPressPreferences.getCustomContentName(phpModule);
            originalWordPressRoot = WordPressPreferences.getWordPressRootPath(phpModule);
            originalPlugins = WordPressPreferences.getPluginsPath(phpModule);
            originalThemes = WordPressPreferences.getThemesPath(phpModule);
            panel.setPluginEnabled(originalEnabled);
            panel.setCustomContentName(originalCustomeContentName);
            panel.setComponentsEnabled(originalEnabled);
            panel.setWordPressRootDirectory(originalWordPressRoot);
            panel.setPluginsDirectory(originalPlugins);
            panel.setThemesDirectory(originalThemes);
        }

        return panel;
    }

}
