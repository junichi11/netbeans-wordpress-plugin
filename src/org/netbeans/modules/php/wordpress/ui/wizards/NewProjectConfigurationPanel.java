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
package org.netbeans.modules.php.wordpress.ui.wizards;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.wordpress.ui.options.WordPressOptions;
import org.netbeans.modules.php.wordpress.util.NetUtils;

/**
 *
 * @author junichi11
 */
public class NewProjectConfigurationPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -736782035524380751L;

    /**
     * Creates new form NewProjectConfigurationPanel
     */
    public NewProjectConfigurationPanel() {
        initComponents();
        useURLRadioButton.setSelected(true);
        init();
    }

    private void init() {
        WordPressOptions options = WordPressOptions.getInstance();
        if (StringUtils.isEmpty(options.getWpCliPath())) {
            useWpCliRadioButton.setEnabled(false);
            useWpCliRadioButton.setVisible(false);
        }

        if (StringUtils.isEmpty(options.getLocalFilePath())) {
            useLocalFileRadioButton.setEnabled(false);
            useLocalFileRadioButton.setVisible(false);
        }

    }

    public JTextField getUnzipStatusTextField() {
        return unzipStatusTextField;
    }

    public void setUnzipStatusTextField(JTextField unzipStatusTextField) {
        this.unzipStatusTextField = unzipStatusTextField;
    }

    public void setLocalFileLabel(String text) {
        localFileLabel.setText(text);
    }

    public void setUrlLabel(String text) {
        urlLabel.setText(text);
    }

    public String getLocalFileLabel() {
        return localFileLabel.getText();
    }

    public String getUrlLabel() {
        return urlLabel.getText();
    }

    public boolean useUrl() {
        return useURLRadioButton.isSelected();
    }

    public boolean useLocalFile() {
        return useLocalFileRadioButton.isSelected();
    }

    public boolean useWpCli() {
        return useWpCliRadioButton.isSelected();
    }

    public boolean useFormatOption() {
        return formatCheckBox.isSelected();
    }

    public void setEnabledUrl(boolean enable) {
        useURLRadioButton.setEnabled(enable);
        urlLabel.setEnabled(enable);
        useLocalFileRadioButton.setSelected(!enable);
    }

    public boolean isSelectedCreateConfig() {
        return createConfigCheckBox.isSelected();
    }

    public JPanel getWpConfigPanel() {
        return wpConfigPanel;
    }

    public void setAllEnabled(JPanel panel, boolean enabled) {
        if (panel != null) {
            for (Component c : panel.getComponents()) {
                c.setEnabled(enabled);
            }
        }
    }

    public String getDbCharset() {
        return dbCharsetTextField.getText();
    }

    public String getDbCollate() {
        return dbCollateTextField.getText();
    }

    public String getDbHost() {
        return dbHostTextField.getText();
    }

    public String getDbName() {
        return dbNameTextField.getText();
    }

    public String getDbPassword() {
        return dbPasswordTextField.getText();
    }

    public String getDbUser() {
        return dbUserTextField.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        unzipLabel = new javax.swing.JLabel();
        unzipStatusTextField = new javax.swing.JTextField();
        useURLRadioButton = new javax.swing.JRadioButton();
        useLocalFileRadioButton = new javax.swing.JRadioButton();
        useWpCliRadioButton = new javax.swing.JRadioButton();
        urlLabel = new javax.swing.JLabel();
        localFileLabel = new javax.swing.JLabel();
        formatCheckBox = new javax.swing.JCheckBox();
        wpConfigPanel = new javax.swing.JPanel();
        dbNameLabel = new javax.swing.JLabel();
        dbNameTextField = new javax.swing.JTextField();
        dbUserLabel = new javax.swing.JLabel();
        dbUserTextField = new javax.swing.JTextField();
        dbPasswordLabel = new javax.swing.JLabel();
        dbHostLabel = new javax.swing.JLabel();
        dbCharsetLabel = new javax.swing.JLabel();
        dbCollateLabel = new javax.swing.JLabel();
        dbPasswordTextField = new javax.swing.JTextField();
        dbHostTextField = new javax.swing.JTextField();
        dbCharsetTextField = new javax.swing.JTextField();
        dbCollateTextField = new javax.swing.JTextField();
        createConfigCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(unzipLabel, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.unzipLabel.text")); // NOI18N

        unzipStatusTextField.setEditable(false);
        unzipStatusTextField.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.unzipStatusTextField.text")); // NOI18N

        buttonGroup1.add(useURLRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(useURLRadioButton, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.useURLRadioButton.text")); // NOI18N
        useURLRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useURLRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(useLocalFileRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(useLocalFileRadioButton, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.useLocalFileRadioButton.text")); // NOI18N
        useLocalFileRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLocalFileRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(useWpCliRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(useWpCliRadioButton, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.useWpCliRadioButton.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(urlLabel, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.urlLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(localFileLabel, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.localFileLabel.text")); // NOI18N

        formatCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(formatCheckBox, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.formatCheckBox.text")); // NOI18N

        wpConfigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.wpConfigPanel.border.title"))); // NOI18N
        wpConfigPanel.setAutoscrolls(true);

        org.openide.awt.Mnemonics.setLocalizedText(dbNameLabel, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbNameLabel.text")); // NOI18N

        dbNameTextField.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbNameTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dbUserLabel, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbUserLabel.text")); // NOI18N

        dbUserTextField.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbUserTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dbPasswordLabel, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbPasswordLabel.text")); // NOI18N
        dbPasswordLabel.setToolTipText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbPasswordLabel.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dbHostLabel, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbHostLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dbCharsetLabel, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbCharsetLabel.text")); // NOI18N
        dbCharsetLabel.setToolTipText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbCharsetLabel.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dbCollateLabel, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbCollateLabel.text")); // NOI18N
        dbCollateLabel.setToolTipText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbCollateLabel.toolTipText")); // NOI18N

        dbPasswordTextField.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbPasswordTextField.text")); // NOI18N

        dbHostTextField.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbHostTextField.text")); // NOI18N

        dbCharsetTextField.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbCharsetTextField.text")); // NOI18N

        dbCollateTextField.setText(org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.dbCollateTextField.text")); // NOI18N

        javax.swing.GroupLayout wpConfigPanelLayout = new javax.swing.GroupLayout(wpConfigPanel);
        wpConfigPanel.setLayout(wpConfigPanelLayout);
        wpConfigPanelLayout.setHorizontalGroup(
            wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wpConfigPanelLayout.createSequentialGroup()
                .addGroup(wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dbNameLabel)
                    .addComponent(dbHostLabel)
                    .addComponent(dbCharsetLabel)
                    .addComponent(dbCollateLabel)
                    .addComponent(dbUserLabel)
                    .addComponent(dbPasswordLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dbNameTextField)
                    .addComponent(dbUserTextField)
                    .addComponent(dbPasswordTextField)
                    .addComponent(dbHostTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                    .addComponent(dbCharsetTextField)
                    .addComponent(dbCollateTextField)))
        );
        wpConfigPanelLayout.setVerticalGroup(
            wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(wpConfigPanelLayout.createSequentialGroup()
                .addGroup(wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbNameLabel)
                    .addComponent(dbNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbUserLabel)
                    .addComponent(dbUserTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbPasswordLabel)
                    .addComponent(dbPasswordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbHostLabel)
                    .addComponent(dbHostTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbCharsetLabel)
                    .addComponent(dbCharsetTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(wpConfigPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dbCollateLabel)
                    .addComponent(dbCollateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        createConfigCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(createConfigCheckBox, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.createConfigCheckBox.text")); // NOI18N
        createConfigCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createConfigCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(unzipLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(unzipStatusTextField))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(useLocalFileRadioButton)
                            .addComponent(useURLRadioButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(urlLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(localFileLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(formatCheckBox)
                            .addComponent(createConfigCheckBox)
                            .addComponent(useWpCliRadioButton))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(wpConfigPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(unzipLabel)
                    .addComponent(unzipStatusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(useURLRadioButton)
                    .addComponent(urlLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(useLocalFileRadioButton)
                    .addComponent(localFileLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(useWpCliRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(formatCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(createConfigCheckBox)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(wpConfigPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void useURLRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useURLRadioButtonActionPerformed
        String text = urlLabel.getText();
        if (text == null || text.isEmpty() || !NetUtils.isInternetReachable(text)) {
            urlLabel.setEnabled(false);
            useURLRadioButton.setSelected(false);
            useLocalFileRadioButton.setSelected(true);
            useURLRadioButton.setEnabled(false);
        }
    }//GEN-LAST:event_useURLRadioButtonActionPerformed

    private void useLocalFileRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLocalFileRadioButtonActionPerformed
        String text = localFileLabel.getText();
        if (text == null || text.isEmpty()) {
            useLocalFileRadioButton.setSelected(false);
            useURLRadioButton.setSelected(true);
            useLocalFileRadioButton.setEnabled(false);
        }
    }//GEN-LAST:event_useLocalFileRadioButtonActionPerformed

    private void createConfigCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createConfigCheckBoxActionPerformed
        // TODO add your handling code here:
        if (!createConfigCheckBox.isSelected()) {
            wpConfigPanel.setEnabled(false);
            setAllEnabled(wpConfigPanel, false);
        } else {
            wpConfigPanel.setEnabled(true);
            setAllEnabled(wpConfigPanel, true);
        }
    }//GEN-LAST:event_createConfigCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox createConfigCheckBox;
    private javax.swing.JLabel dbCharsetLabel;
    private javax.swing.JTextField dbCharsetTextField;
    private javax.swing.JLabel dbCollateLabel;
    private javax.swing.JTextField dbCollateTextField;
    private javax.swing.JLabel dbHostLabel;
    private javax.swing.JTextField dbHostTextField;
    private javax.swing.JLabel dbNameLabel;
    private javax.swing.JTextField dbNameTextField;
    private javax.swing.JLabel dbPasswordLabel;
    private javax.swing.JTextField dbPasswordTextField;
    private javax.swing.JLabel dbUserLabel;
    private javax.swing.JTextField dbUserTextField;
    private javax.swing.JCheckBox formatCheckBox;
    private javax.swing.JLabel localFileLabel;
    private javax.swing.JLabel unzipLabel;
    private javax.swing.JTextField unzipStatusTextField;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JRadioButton useLocalFileRadioButton;
    private javax.swing.JRadioButton useURLRadioButton;
    private javax.swing.JRadioButton useWpCliRadioButton;
    private javax.swing.JPanel wpConfigPanel;
    // End of variables declaration//GEN-END:variables
}
