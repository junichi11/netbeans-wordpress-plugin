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
package org.netbeans.modules.php.wordpress.ui.status;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.wordpress.WordPress;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.modules.WordPressModule.DIR_TYPE;
import org.netbeans.modules.php.wordpress.util.Charset;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.openide.awt.StatusLineElementProvider;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author junichi11
 */
@ServiceProvider(service = StatusLineElementProvider.class)
public class DebugStatusLineElement implements StatusLineElementProvider {

    private static final Logger LOGGER = Logger.getLogger(DebugStatusLineElement.class.getName());
    private static final String DEBUG_TRUE = "true"; // NOI18N
    private static final String DEBUG_FALSE = "false"; // NOI18N
    private static final String WP_DEBUG_FORMAT = "define('WP_DEBUG', %s);"; // NOI18N
    private static final String DEBUG_REGEX = "^define\\(\\s*'WP_DEBUG',\\s*(true|false)\\s*\\);$"; // NOI18N
    private static final String VERSION_REGEX = "^\\$wp_version\\s*=\\s*'(.+)';$"; // NOI18N
    private static final Map<String, String> debugLevel = new HashMap<>();
    private static final String WP_CONFIG_PHP = "wp-config.php"; // NOI18N
    private final ImageIcon icon = ImageUtilities.loadImageIcon(WordPress.WP_ICON_16, true);
    private final Lookup.Result<FileObject> result;
    private final JLabel debugLabel = new JLabel(""); // NOI18N
    private final JLabel versionLabel = new JLabel(""); // NOI18N
    private final DefaultListModel<String> model;
    private PhpModule phpModule;
    private JList<String> list;
    private Popup popup;
    private String level = ""; // NOI18N
    private String version = "";  // NOI18N
    private boolean popupFlg = false;

    static {
        debugLevel.put(DEBUG_TRUE, DEBUG_TRUE); // NOI18N
        debugLevel.put(DEBUG_FALSE, DEBUG_FALSE); // NOI18N
    }

    public DebugStatusLineElement() {
        result = Utilities.actionsGlobalContext().lookupResult(FileObject.class);
        result.addLookupListener(new LookupListenerImpl());

        model = new DefaultListModel<>();
        for (String debugLv : debugLevel.keySet()) {
            model.addElement(debugLv);
        }
        list = new JList<>(model);

        // add MouseAdapter
        debugLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                Point labelStart = debugLabel.getLocationOnScreen();
                int x = Math.min(labelStart.x, labelStart.x + debugLabel.getSize().width - list.getPreferredSize().width);
                int y = labelStart.y - list.getPreferredSize().height;
                if (popup == null) {
                    popup = PopupFactory.getSharedInstance().getPopup(debugLabel, list, x, y);
                }
                list.addListSelectionListener((ListSelectionEvent e1) -> {
                    String debugLv = list.getSelectedValue();
                    // write file
                    if (!debugLv.equals(level)) {
                        writeConfig(debugLv);
                    }
                    popupFlg = false;
                    if (popup != null) {
                        popup.hide();
                        popup = null;
                    }
                });
                if (!popupFlg) {
                    popupFlg = true;
                    popup.show();
                } else {
                    popupFlg = false;
                    popup.hide();
                    popup = null;
                }
            }
        });
    }

    /**
     * Write config file.
     *
     * @param debugLv true or false
     */
    private void writeConfig(final String debugLv) {
        WordPressModule wpModule = WordPressModule.Factory.forPhpModule(phpModule);
        FileObject config = wpModule.getDirecotry(DIR_TYPE.ROOT, WP_CONFIG_PHP);
        if (config == null) {
            LOGGER.log(Level.WARNING, "Not found wp-config.php");
            return;
        }
        try {
            Lookup lookup = config.getLookup();
            EditorCookie ec = lookup.lookup(EditorCookie.class);
            if (ec == null) {
                return;
            }
            final StyledDocument docment = ec.openDocument();
            if (docment == null) {
                return;
            }
            List<String> lines = config.asLines(Charset.UTF8);
            Pattern pattern = Pattern.compile(DEBUG_REGEX);
            int lineNumber = 0;
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    // change
                    final int startOffset = NbDocument.findLineOffset(docment, lineNumber);
                    final int removeLength = line.length();

                    NbDocument.runAtomic(docment, () -> {
                        try {
                            docment.remove(startOffset, removeLength);
                            docment.insertString(startOffset, String.format(WP_DEBUG_FORMAT, debugLv), null);
                        } catch (BadLocationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    });
                }
                lineNumber++;
            }
            ec.saveDocument();
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    @Override
    public Component getStatusLineElement() {
        return panelWithSeparator(debugLabel);
    }

    /**
     * Add separator to component.
     *
     * @param cell JLabel
     * @return Component
     */
    private Component panelWithSeparator(JLabel cell) {
        JSeparator separator = new JSeparatorImpl(SwingConstants.VERTICAL);
        separator.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(separator, BorderLayout.WEST);
        panel.add(versionLabel, BorderLayout.CENTER);
        panel.add(cell, BorderLayout.EAST);
        return panel;
    }

    /**
     * Get WordPress version
     *
     * @param version version.php
     * @return version
     */
    public String getVersion(FileObject version) {
        String versionNumber = ""; // NOI18N
        Pattern pattern = Pattern.compile(VERSION_REGEX);

        try {
            List<String> lines = version.asLines();
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    versionNumber = matcher.group(1);
                    break;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return versionNumber;
    }

    /**
     * Get debug level
     *
     * @param config wp-config.php
     * @return debug level
     */
    public String getDebugLevel(FileObject config) {
        String debugLv = ""; // NOI18N
        Pattern pattern = Pattern.compile(DEBUG_REGEX);

        try {
            List<String> lines = config.asLines();
            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    debugLv = matcher.group(1);
                    break;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return debugLv;
    }

    /**
     * Set debug debugLv.
     *
     * @param debugLv true or false
     */
    private void setDebugLevelLabel(final String debugLv) {
        SwingUtilities.invokeLater(() -> {
            if (debugLv.matches("^(true|false)$")) { // NOI18N
                debugLabel.setText(debugLevel.get(debugLv));
            } else {
                debugLabel.setText(debugLv);
            }
        });
    }

    /**
     * Set version versionLv.
     *
     * @param versionNumber
     */
    private void setVersionLabel(final String versionNumber) {
        SwingUtilities.invokeLater(() -> {
            versionLabel.setText(versionNumber);
            versionLabel.setIcon(icon);
        });
    }

    /**
     * Clear debug label
     */
    private void clearLabel() {
        SwingUtilities.invokeLater(() -> {
            debugLabel.setText(""); //NOI18N
            versionLabel.setText(""); // NOI18N
            versionLabel.setIcon(null);
        });
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setPhpModule(PhpModule phpModule) {
        this.phpModule = phpModule;
    }

    public PhpModule getPhpModule() {
        return phpModule;
    }

    //~ Inner class
    private class LookupListenerImpl implements LookupListener {

        private final FileChangeAdapter fileChangeAdapter;

        public LookupListenerImpl() {
            fileChangeAdapter = new FileChangeAdapter() {
                @Override
                public void fileChanged(FileEvent fe) {
                    String level = getDebugLevel(fe.getFile());
                    setLevel(level);
                    setDebugLevelLabel(level);
                }
            };
        }

        @Override
        public void resultChanged(LookupEvent lookupEvent) {
            Lookup.Result<?> lookupResult = (Lookup.Result<?>) lookupEvent.getSource();
            Collection<?> c = lookupResult.allInstances();

            // get FileObject
            FileObject fileObject;
            if (!c.isEmpty()) {
                fileObject = (FileObject) c.iterator().next();
            } else {
                clearLabel();
                return;
            }

            // check whether project is WordPress
            PhpModule pmTemp = PhpModule.Factory.forFileObject(fileObject);
            if (!WPUtils.isWP(pmTemp)) {
                clearLabel();
                return;
            }

            // check whether phpmodule is changed
            PhpModule pm = getPhpModule();
            if (pm == pmTemp) {
                setDebugLevelLabel(getLevel());
                setVersionLabel(getVersion());
                return;
            } else {
                if (pm != null) {
                    // remove file change listener
                    removeFileChangeListenerForConfig(pm);
                }
                pm = pmTemp;
                setPhpModule(pm);
            }

            // if it is other project, add FileChangeListener to FileObject
            WordPressModule wpModule = WordPressModule.Factory.forPhpModule(phpModule);
            FileObject config = wpModule.getDirecotry(DIR_TYPE.ROOT, WP_CONFIG_PHP);
            if (config == null) {
                return;
            }

            // add file change listener
            config.addFileChangeListener(fileChangeAdapter);

            String level = getDebugLevel(config);
            setLevel(level);
            list.setSelectedValue(level, true);
            setDebugLevelLabel(level);

            // version
            FileObject version = wpModule.getVersionFile();
            String versionNumber = ""; // NOI18N
            if (version != null) {
                versionNumber = getVersion(version) + ":"; // NOI18N
            }
            setVersion(versionNumber);
            setVersionLabel(versionNumber);
        }

        private void removeFileChangeListenerForConfig(PhpModule pm) {
            WordPressModule wpModule = WordPressModule.Factory.forPhpModule(pm);
            FileObject config = wpModule.getDirecotry(DIR_TYPE.ROOT, WP_CONFIG_PHP);
            if (config != null) {
                config.removeFileChangeListener(fileChangeAdapter);
            }
        }
    }

    private static class JSeparatorImpl extends JSeparator {

        private static final long serialVersionUID = -6385848933295984637L;

        public JSeparatorImpl(int orientation) {
            super(orientation);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(3, 3);
        }
    }
}
