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
package org.netbeans.modules.php.wordpress.ui.options;

import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.util.UiUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.netbeans.modules.php.wordpress.commands.WordPressCli;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

final class WordPressOptionsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -4504251144555676048L;
    private static final String ZIP = ".zip"; // NOI18N
    private static final String WP_CLI_LAST_FOLDER_SUFFIX = ".wp-cli"; // NOI18N
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private String wpCliPath;
    private static final Logger LOGGER = Logger.getLogger(WordPressOptionsPanel.class.getName());

    WordPressOptionsPanel() {
        initComponents();
        init();
    }

    @NbBundle.Messages({
        "# {0} - short script name",
        "# {1} - long script name",
        "WordPressOptionsPanel.hint=Full path of wp-cli script (typically {0} or {1})"})
    private void init() {
        wpCliVersionLabel.setText(""); // NOI18N
        errorLabel.setText(" "); // NOI18N
        hintLabel.setText(Bundle.WordPressOptionsPanel_hint(WordPressCli.NAME, WordPressCli.LONG_NAME));
        // listener
        wpCliPathTextField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                processUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                processUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                processUpdate();
            }

            private void processUpdate() {
                fireChange();
            }
        });
        wpCliVersionLabel.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                boolean isEnabled = !wpCliVersionLabel.getText().isEmpty();
                checkPluginNewVersionCheckBox.setEnabled(isEnabled);
                checkThemeNewVersionCheckBox.setEnabled(isEnabled);
            }
        });
    }

    public String getWpLocale() {
        return localeTextField.getText().trim();
    }

    public void setWpLocale(String locale) {
        localeTextField.setText(locale);
    }

    public boolean isCheckCoreNewVersion() {
        return checkCoreNewVersionCheckBox.isSelected();
    }

    public void setCheckCoreNewVersion(boolean check) {
        checkCoreNewVersionCheckBox.setSelected(check);
    }

    public boolean isCheckPluginNewVersion() {
        return checkPluginNewVersionCheckBox.isSelected();
    }

    public void setCheckPluginNewVersion(boolean check) {
        checkPluginNewVersionCheckBox.setSelected(check);
    }

    public boolean isCheckThemeNewVersion() {
        return checkThemeNewVersionCheckBox.isSelected();
    }

    public void setCheckThemeNewVersion(boolean check) {
        checkThemeNewVersionCheckBox.setSelected(check);
    }

    public String getWpCliPath() {
        return wpCliPathTextField.getText().trim();
    }

    public String getWpCliLocale() {
        return wpCliDownloadLocaleTextField.getText().trim();
    }

    public String getWpCliVersion() {
        return wpCliDownloadVersionTextField.getText().trim();
    }

    public boolean isWpCliGetCommandsOnBoot() {
        return wpCliGetCommandsOnBootCheckBox.isSelected();
    }

    public void setLocalPath(String path) {
        localFileTextField.setText(path);
    }

    public void setUrl(String url) {
        downloadUrlTextField.setText(url);
    }

    public void setWpCliPath(String path) {
        wpCliPathTextField.setText(path);
    }

    public void setWpCliDownloadLocale(String locale) {
        wpCliDownloadLocaleTextField.setText(locale);
    }

    public void setWpCliDownloadVersion(String locale) {
        wpCliDownloadVersionTextField.setText(locale);
    }

    public void setWpCliGetCommandsOnBoot(boolean get) {
        wpCliGetCommandsOnBootCheckBox.setSelected(get);
    }

    private WordPressOptions getOptions() {
        return WordPressOptions.getInstance();
    }

    public void setError(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.errorForeground")); // NOI18N
        errorLabel.setText(message);
    }

    public void setWarning(String message) {
        errorLabel.setText(" "); // NOI18N
        errorLabel.setForeground(UIManager.getColor("nb.warningForeground")); // NOI18N
        errorLabel.setText(message);
    }

    private void setWpCliVersoin() {
        if (!StringUtils.isEmpty(getWpCliPath())) {
            try {
                WordPressCli wpCli = WordPressCli.getDefault(true);
                String version = wpCli.getVersion();
                wpCliVersionLabel.setText(version);
            } catch (InvalidPhpExecutableException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            wpCliVersionLabel.setText(""); // NOI18N
        }
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    void fireChange() {
        changeSupport.fireChange();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        newProjectLabel = new javax.swing.JLabel();
        newProjectSeparator = new javax.swing.JSeparator();
        downloadUrlLabel = new javax.swing.JLabel();
        localFilePathLabel = new javax.swing.JLabel();
        downloadUrlTextField = new javax.swing.JTextField();
        localFileTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        wpCliLabel = new javax.swing.JLabel();
        wpCliVersionLabel = new javax.swing.JLabel();
        wpCliSeparator = new javax.swing.JSeparator();
        wpCliPathLabel = new javax.swing.JLabel();
        wpCliPathTextField = new javax.swing.JTextField();
        hintLabel = new javax.swing.JLabel();
        wpCliSearchButton = new javax.swing.JButton();
        wpCliBrowseButton = new javax.swing.JButton();
        noteLabel = new javax.swing.JLabel();
        learnMoreWpCliLabel = new javax.swing.JLabel();
        wpCliDownloadLabel = new javax.swing.JLabel();
        wpCliDownloadLocaleLabel = new javax.swing.JLabel();
        wpCliDownloadLocaleTextField = new javax.swing.JTextField();
        wpCliDownloadVersionLabel = new javax.swing.JLabel();
        wpCliDownloadVersionTextField = new javax.swing.JTextField();
        errorLabel = new javax.swing.JLabel();
        wpCliGetCommandsOnBootCheckBox = new javax.swing.JCheckBox();
        generalLabel = new javax.swing.JLabel();
        localeLabel = new javax.swing.JLabel();
        localeTextField = new javax.swing.JTextField();
        newProjectSeparator1 = new javax.swing.JSeparator();
        checkCoreNewVersionCheckBox = new javax.swing.JCheckBox();
        checkNewVersionLabel = new javax.swing.JLabel();
        checkPluginNewVersionCheckBox = new javax.swing.JCheckBox();
        checkThemeNewVersionCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(newProjectLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.newProjectLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(downloadUrlLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.downloadUrlLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(localFilePathLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.localFilePathLabel.text")); // NOI18N

        downloadUrlTextField.setText(org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.downloadUrlTextField.text")); // NOI18N

        localFileTextField.setText(org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.localFileTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(wpCliLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(wpCliVersionLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliVersionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(wpCliPathLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliPathLabel.text")); // NOI18N

        wpCliPathTextField.setText(org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliPathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(hintLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.hintLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(wpCliSearchButton, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliSearchButton.text")); // NOI18N
        wpCliSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpCliSearchButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(wpCliBrowseButton, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliBrowseButton.text")); // NOI18N
        wpCliBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wpCliBrowseButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(noteLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.noteLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(learnMoreWpCliLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.learnMoreWpCliLabel.text")); // NOI18N
        learnMoreWpCliLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                learnMoreWpCliLabelMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                learnMoreWpCliLabelMousePressed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(wpCliDownloadLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliDownloadLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(wpCliDownloadLocaleLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliDownloadLocaleLabel.text")); // NOI18N

        wpCliDownloadLocaleTextField.setText(org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliDownloadLocaleTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(wpCliDownloadVersionLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliDownloadVersionLabel.text")); // NOI18N

        wpCliDownloadVersionTextField.setText(org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliDownloadVersionTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(errorLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.errorLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(wpCliGetCommandsOnBootCheckBox, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.wpCliGetCommandsOnBootCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(generalLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.generalLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(localeLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.localeLabel.text")); // NOI18N

        localeTextField.setText(org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.localeTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(checkCoreNewVersionCheckBox, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.checkCoreNewVersionCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(checkNewVersionLabel, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.checkNewVersionLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(checkPluginNewVersionCheckBox, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.checkPluginNewVersionCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(checkThemeNewVersionCheckBox, org.openide.util.NbBundle.getMessage(WordPressOptionsPanel.class, "WordPressOptionsPanel.checkThemeNewVersionCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(newProjectLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(newProjectSeparator))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(learnMoreWpCliLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(errorLabel))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(12, 12, 12))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(wpCliLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wpCliVersionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wpCliSeparator)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(downloadUrlLabel)
                                        .addComponent(localFilePathLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(wpCliPathLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(downloadUrlTextField)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(wpCliPathTextField)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(wpCliBrowseButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(wpCliSearchButton))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(hintLabel)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(localFileTextField)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(browseButton))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(wpCliGetCommandsOnBootCheckBox)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(wpCliDownloadLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(wpCliDownloadLocaleLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(wpCliDownloadLocaleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(wpCliDownloadVersionLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(wpCliDownloadVersionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(generalLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(newProjectSeparator1))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(localeLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(localeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(checkNewVersionLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkCoreNewVersionCheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkPluginNewVersionCheckBox)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(checkThemeNewVersionCheckBox)))))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(generalLabel)
                    .addComponent(newProjectSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(localeLabel)
                    .addComponent(localeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(checkCoreNewVersionCheckBox)
                    .addComponent(checkNewVersionLabel)
                    .addComponent(checkPluginNewVersionCheckBox)
                    .addComponent(checkThemeNewVersionCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(newProjectLabel)
                            .addComponent(newProjectSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(downloadUrlLabel)
                            .addComponent(downloadUrlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(localFilePathLabel)
                            .addComponent(localFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(browseButton))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(wpCliLabel)
                            .addComponent(wpCliVersionLabel)))
                    .addComponent(wpCliSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wpCliPathLabel)
                    .addComponent(wpCliPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wpCliBrowseButton)
                    .addComponent(wpCliSearchButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hintLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wpCliDownloadLabel)
                    .addComponent(wpCliDownloadLocaleLabel)
                    .addComponent(wpCliDownloadLocaleTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(wpCliDownloadVersionLabel)
                    .addComponent(wpCliDownloadVersionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wpCliGetCommandsOnBootCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noteLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(learnMoreWpCliLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("LBL_LocalFilePath=Local file path")
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        File localFile = new FileChooserBuilder(WordPressOptionsPanel.class.getName())
                .setTitle(Bundle.LBL_LocalFilePath())
                .setFilesOnly(true)
                .showOpenDialog();
        if (localFile != null) {
            setLocalPath(localFile.getAbsolutePath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    @NbBundle.Messages({
        "WordPressOptionsPanel.search.scripts.title=wp-cli scripts",
        "WordPressOptionsPanel.search.scripts=&wp-cli scripts:",
        "WordPressOptionsPanel.search.scripts.pleaseWaitPart=wp-cli scripts",
        "WordPressOptionsPanel.search.scripts.notFound=No wp-cli scripts found."
    })
    private void wpCliSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wpCliSearchButtonActionPerformed
        String script = UiUtils.SearchWindow.search(new UiUtils.SearchWindow.SearchWindowSupport() {
            @Override
            public List<String> detect() {
                return FileUtils.findFileOnUsersPath(WordPressCli.NAME, WordPressCli.LONG_NAME);
            }

            @Override
            public String getWindowTitle() {
                return Bundle.WordPressOptionsPanel_search_scripts_title();
            }

            @Override
            public String getListTitle() {
                return Bundle.WordPressOptionsPanel_search_scripts();
            }

            @Override
            public String getPleaseWaitPart() {
                return Bundle.WordPressOptionsPanel_search_scripts_pleaseWaitPart();
            }

            @Override
            public String getNoItemsFound() {
                return Bundle.WordPressOptionsPanel_search_scripts_notFound();
            }
        });
        if (script != null) {
            wpCliPathTextField.setText(script);
            store();
        }
    }//GEN-LAST:event_wpCliSearchButtonActionPerformed

    @NbBundle.Messages("WordPressOptionsPanel.browse.title=Select wp-cli script")
    private void wpCliBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wpCliBrowseButtonActionPerformed
        File wp = new FileChooserBuilder(WordPressOptionsPanel.class.getName() + WP_CLI_LAST_FOLDER_SUFFIX)
                .setTitle(Bundle.WordPressOptionsPanel_browse_title())
                .setFilesOnly(true)
                .showOpenDialog();
        if (wp != null) {
            wp = FileUtil.normalizeFile(wp);
            String wpPath = wp.getAbsolutePath();
            wpCliPathTextField.setText(wpPath);
            store();
        }
    }//GEN-LAST:event_wpCliBrowseButtonActionPerformed

    private void learnMoreWpCliLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_learnMoreWpCliLabelMousePressed
        try {
            URL url = new URL("http://wp-cli.org/"); // NOI18N
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_learnMoreWpCliLabelMousePressed

    private void learnMoreWpCliLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_learnMoreWpCliLabelMouseEntered
        evt.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_learnMoreWpCliLabelMouseEntered

    void load() {
        setWpLocale(getOptions().getWpLocale());
        setCheckCoreNewVersion(getOptions().isCheckCoreNewVersion());
        setCheckPluginNewVersion(getOptions().isCheckPluginNewVersion());
        setCheckThemeNewVersion(getOptions().isCheckThemeNewVersion());
        setUrl(getOptions().getDownloadUrl());
        setLocalPath(getOptions().getLocalFilePath());
        wpCliPath = getOptions().getWpCliPath();
        setWpCliPath(wpCliPath);
        setWpCliDownloadLocale(getOptions().getWpCliDownloadLocale());
        setWpCliDownloadVersion(getOptions().getWpCliDownloadVersion());
        setWpCliGetCommandsOnBoot(getOptions().getWpCliGetCommandsOnBoot());
        setWpCliVersoin();
    }

    void store() {
        getOptions().setWpLocale(getWpLocale());
        getOptions().setCheckCoreNewVersion(isCheckCoreNewVersion());
        getOptions().setCheckPluginNewVersion(isCheckPluginNewVersion());
        getOptions().setCheckThemeNewVersion(isCheckThemeNewVersion());
        String url = downloadUrlTextField.getText();
        String localFile = localFileTextField.getText();

        if (url != null) {
            String old = getOptions().getDownloadUrl();
            if (!old.equals(url)) {
                getOptions().setDownloadUrl(url);
            }
        }

        if (localFile != null && !localFile.isEmpty()) {
            String old = getOptions().getLocalFilePath();
            if (!old.equals(localFile) && localFile.endsWith(ZIP)) {
                getOptions().setLocalFilePath(localFile);
            }
            getOptions().setLocalFilePath(localFile);
        }
        // wp-cli
        getOptions().setWpCliPath(getWpCliPath());
        getOptions().setWpCliDownloadLocale(getWpCliLocale());
        getOptions().setWpCliDownloadVersion(getWpCliVersion());
        getOptions().setWpCliGetCommandsOnBoot(isWpCliGetCommandsOnBoot());
        setWpCliVersoin();
        // update command list
        String newWpCliPath = getWpCliPath();
        if (StringUtils.isEmpty(newWpCliPath)) {
            getOptions().setWpCliCommandList(""); // NOI18N
            getOptions().setCheckPluginNewVersion(false);
            getOptions().setCheckThemeNewVersion(false);
        }
        if (!StringUtils.isEmpty(newWpCliPath) && !newWpCliPath.equals(wpCliPath)) {
            wpCliPath = newWpCliPath;
            updateCommandListXml();
        }
    }

    @NbBundle.Messages("WordPressOptionsPanel.update.command.progress=Updating wp-cli command list")
    private void updateCommandListXml() {
        RequestProcessor.getDefault().post(new Runnable() {

            @Override
            public void run() {
                ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.WordPressOptionsPanel_update_command_progress());
                try {
                    handle.start();
                    try {
                        WordPressCli wpCli = WordPressCli.getDefault(false);
                        wpCli.updateCommands();
                    } catch (InvalidPhpExecutableException ex) {
                        LOGGER.log(Level.WARNING, ex.getLocalizedMessage());
                    }

                } finally {
                    handle.finish();
                }
            }
        });
    }

    boolean valid() {
        ValidationResult result = new WordPressOptionsValidator()
                .validate(getWpCliPath())
                .getResult();
        // errors
        if (result.hasErrors()) {
            setError(result.getErrors().get(0).getMessage());
            return false;
        }

        // warnings
        if (result.hasWarnings()) {
            setWarning(result.getWarnings().get(0).getMessage());
            return true;
        }

        // everything ok
        setError(" "); // NOI18N
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JCheckBox checkCoreNewVersionCheckBox;
    private javax.swing.JLabel checkNewVersionLabel;
    private javax.swing.JCheckBox checkPluginNewVersionCheckBox;
    private javax.swing.JCheckBox checkThemeNewVersionCheckBox;
    private javax.swing.JLabel downloadUrlLabel;
    private javax.swing.JTextField downloadUrlTextField;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel generalLabel;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JLabel learnMoreWpCliLabel;
    private javax.swing.JLabel localFilePathLabel;
    private javax.swing.JTextField localFileTextField;
    private javax.swing.JLabel localeLabel;
    private javax.swing.JTextField localeTextField;
    private javax.swing.JLabel newProjectLabel;
    private javax.swing.JSeparator newProjectSeparator;
    private javax.swing.JSeparator newProjectSeparator1;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JButton wpCliBrowseButton;
    private javax.swing.JLabel wpCliDownloadLabel;
    private javax.swing.JLabel wpCliDownloadLocaleLabel;
    private javax.swing.JTextField wpCliDownloadLocaleTextField;
    private javax.swing.JLabel wpCliDownloadVersionLabel;
    private javax.swing.JTextField wpCliDownloadVersionTextField;
    private javax.swing.JCheckBox wpCliGetCommandsOnBootCheckBox;
    private javax.swing.JLabel wpCliLabel;
    private javax.swing.JLabel wpCliPathLabel;
    private javax.swing.JTextField wpCliPathTextField;
    private javax.swing.JButton wpCliSearchButton;
    private javax.swing.JSeparator wpCliSeparator;
    private javax.swing.JLabel wpCliVersionLabel;
    // End of variables declaration//GEN-END:variables
}
