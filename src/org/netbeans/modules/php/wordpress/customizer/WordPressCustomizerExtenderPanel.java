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

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author junichi11
 */
public class WordPressCustomizerExtenderPanel extends JPanel {

    private static final long serialVersionUID = -290290625888827119L;

    private final ChangeSupport changeSupport = new ChangeSupport(this);

    /**
     * Creates new form WordPressCustomizerExtenderPanel
     */
    public WordPressCustomizerExtenderPanel() {
        initComponents();
        init();
    }

    private void init() {
        DefaultDocumentListener defaultDocumentListener = new DefaultDocumentListener();
        addDocumentListener(defaultDocumentListener);
    }

    private void addDocumentListener(DocumentListener listener) {
        customContentNameTextField.getDocument().addDocumentListener(listener);
        wordPressRootTextField.getDocument().addDocumentListener(listener);
        pluginsTextField.getDocument().addDocumentListener(listener);
        themesTextField.getDocument().addDocumentListener(listener);
    }

    public boolean isPluginEnabled() {
        return enabledCheckBox.isSelected();
    }

    public void setPluginEnabled(boolean isEnabled) {
        enabledCheckBox.setSelected(isEnabled);
    }

    public String getCustomContentName() {
        return customContentNameTextField.getText().trim();
    }

    public void setCustomContentName(String name) {
        customContentNameTextField.setText(name);
    }

    public String getWordPressRootDirectory() {
        return wordPressRootTextField.getText().trim();
    }

    public void setWordPressRootDirectory(String path) {
        wordPressRootTextField.setText(path);
    }

    public String getPluginsDirectory() {
        return pluginsTextField.getText().trim();
    }

    public void setPluginsDirectory(String path) {
        pluginsTextField.setText(path);
    }

    public String getThemesDirectory() {
        return themesTextField.getText().trim();
    }

    public void setThemesDirectory(String path) {
        themesTextField.setText(path);
    }

    public void addChangeListener(ChangeListener cl) {
        changeSupport.addChangeListener(cl);
    }

    public void removeChangeListener(ChangeListener cl) {
        changeSupport.removeChangeListener(cl);
    }

    public void fireChange() {
        changeSupport.fireChange();
    }

    public void setComponentsEnabled(boolean isEnabled) {
        Component[] components = this.getComponents();
        for (Component component : components) {
            if (component == enabledCheckBox) {
                continue;
            }
            component.setEnabled(isEnabled);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enabledCheckBox = new javax.swing.JCheckBox();
        customContentNameLabel = new javax.swing.JLabel();
        customContentNameTextField = new javax.swing.JTextField();
        customDirectoryPathLabel = new javax.swing.JLabel();
        wordPressRootLabel = new javax.swing.JLabel();
        wordPressRootTextField = new javax.swing.JTextField();
        pluginsLabel = new javax.swing.JLabel();
        pluginsTextField = new javax.swing.JTextField();
        themesLabel = new javax.swing.JLabel();
        themesTextField = new javax.swing.JTextField();

        setToolTipText(org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(enabledCheckBox, org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.enabledCheckBox.text")); // NOI18N
        enabledCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enabledCheckBoxActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(customContentNameLabel, org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.customContentNameLabel.text")); // NOI18N

        customContentNameTextField.setText(org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.customContentNameTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(customDirectoryPathLabel, org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.customDirectoryPathLabel.text")); // NOI18N
        customDirectoryPathLabel.setToolTipText(org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.customDirectoryPathLabel.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(wordPressRootLabel, org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.wordPressRootLabel.text")); // NOI18N

        wordPressRootTextField.setText(org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.wordPressRootTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(pluginsLabel, org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.pluginsLabel.text")); // NOI18N

        pluginsTextField.setText(org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.pluginsTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(themesLabel, org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.themesLabel.text")); // NOI18N

        themesTextField.setText(org.openide.util.NbBundle.getMessage(WordPressCustomizerExtenderPanel.class, "WordPressCustomizerExtenderPanel.themesTextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(customContentNameLabel)
                            .addComponent(enabledCheckBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(customContentNameTextField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(customDirectoryPathLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wordPressRootLabel)
                            .addComponent(pluginsLabel)
                            .addComponent(themesLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(wordPressRootTextField)
                            .addComponent(pluginsTextField)
                            .addComponent(themesTextField))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(enabledCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(customContentNameLabel)
                    .addComponent(customContentNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(customDirectoryPathLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(wordPressRootLabel)
                    .addComponent(wordPressRootTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pluginsLabel)
                    .addComponent(pluginsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(themesLabel)
                    .addComponent(themesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void enabledCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enabledCheckBoxActionPerformed
        setComponentsEnabled(enabledCheckBox.isSelected());
        changeSupport.fireChange();
    }//GEN-LAST:event_enabledCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel customContentNameLabel;
    private javax.swing.JTextField customContentNameTextField;
    private javax.swing.JLabel customDirectoryPathLabel;
    private javax.swing.JCheckBox enabledCheckBox;
    private javax.swing.JLabel pluginsLabel;
    private javax.swing.JTextField pluginsTextField;
    private javax.swing.JLabel themesLabel;
    private javax.swing.JTextField themesTextField;
    private javax.swing.JLabel wordPressRootLabel;
    private javax.swing.JTextField wordPressRootTextField;
    // End of variables declaration//GEN-END:variables

    private class DefaultDocumentListener implements DocumentListener {

        public DefaultDocumentListener() {
        }

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
    }
}
