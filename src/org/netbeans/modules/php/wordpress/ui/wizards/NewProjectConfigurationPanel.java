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
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 *
 * @author junichi11
 */
public class NewProjectConfigurationPanel extends JPanel {

    private static final long serialVersionUID = -2999785039732460223L;

    private final WpConfigPanel wpConfigPanel = new WpConfigPanel();

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

    public void setAllEnabled(JPanel panel, boolean enabled) {
        if (panel != null) {
            for (Component c : panel.getComponents()) {
                c.setEnabled(enabled);
            }
        }
    }

    public String getDbCharset() {
        return wpConfigPanel.getDbCharset();
    }

    public String getDbCollate() {
        return wpConfigPanel.getDbCollate();
    }

    public String getDbHost() {
        return wpConfigPanel.getDbHost();
    }

    public String getDbName() {
        return wpConfigPanel.getDbName();
    }

    public String getDbPassword() {
        return wpConfigPanel.getDbPassword();
    }

    public String getDbUser() {
        return wpConfigPanel.getDbUser();
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
        createConfigCheckBox = new javax.swing.JCheckBox();
        setWpConfigValuesButton = new javax.swing.JButton();

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

        createConfigCheckBox.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(createConfigCheckBox, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.createConfigCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(setWpConfigValuesButton, org.openide.util.NbBundle.getMessage(NewProjectConfigurationPanel.class, "NewProjectConfigurationPanel.setWpConfigValuesButton.text")); // NOI18N
        setWpConfigValuesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setWpConfigValuesButtonActionPerformed(evt);
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
                            .addComponent(useWpCliRadioButton)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(createConfigCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(setWpConfigValuesButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createConfigCheckBox)
                    .addComponent(setWpConfigValuesButton))
                .addGap(0, 12, Short.MAX_VALUE))
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

    private void setWpConfigValuesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setWpConfigValuesButtonActionPerformed
        wpConfigPanel.store();
        DialogDescriptor dialogDescriptor = new DialogDescriptor(wpConfigPanel, "wp-config", true, DialogDescriptor.OK_CANCEL_OPTION, null, null); // NOI18N
        Object result = DialogDisplayer.getDefault().notify(dialogDescriptor);
        if (result != DialogDescriptor.OK_OPTION) {
            wpConfigPanel.restore();
        }
    }//GEN-LAST:event_setWpConfigValuesButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox createConfigCheckBox;
    private javax.swing.JCheckBox formatCheckBox;
    private javax.swing.JLabel localFileLabel;
    private javax.swing.JButton setWpConfigValuesButton;
    private javax.swing.JLabel unzipLabel;
    private javax.swing.JTextField unzipStatusTextField;
    private javax.swing.JLabel urlLabel;
    private javax.swing.JRadioButton useLocalFileRadioButton;
    private javax.swing.JRadioButton useURLRadioButton;
    private javax.swing.JRadioButton useWpCliRadioButton;
    // End of variables declaration//GEN-END:variables
}
