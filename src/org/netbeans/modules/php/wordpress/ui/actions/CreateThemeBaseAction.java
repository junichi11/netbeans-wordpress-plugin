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
package org.netbeans.modules.php.wordpress.ui.actions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.netbeans.modules.php.wordpress.util.GithubZipEntryFilter;
import org.netbeans.modules.php.wordpress.util.WPFileUtils;
import org.netbeans.modules.php.wordpress.util.ZipEntryFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 *
 * @author junichi11
 */
public abstract class CreateThemeBaseAction extends BaseAction {

    private static final long serialVersionUID = -3421094528883827797L;
    private static final Logger LOGGER = Logger.getLogger(CreateThemeBaseAction.class.getName());
    private Set<String> existingThemeNames;
    private FileObject themesDirectory;

    protected abstract String getName();

    protected abstract String getUrl();

    @Override
    protected String getFullName() {
        return getPureName();
    }

    @Override
    protected String getPureName() {
        return getName();
    }

    @NbBundle.Messages({
        "LBL_ThemeDirectoryName=Theme directory name",
        "# {0} - theme name",
        "LBL_ThemeDirectoryNameTitle=Theme directory name : {0}",
        "# {0} - not found message",
        "LBL_NotFound=Not Found : {0}",
        "LBL_ThemesDirectory=themes directory",
        "LBL_ThemeDirectory=theme directory",
        "LBL_ThemeName=theme name"
    })
    @Override
    protected void actionPerformed(PhpModule phpModule) {
        // get Themes directory
        themesDirectory = WPFileUtils.getThemesDirectory(phpModule);
        if (themesDirectory == null) {
            LOGGER.log(Level.WARNING, Bundle.LBL_NotFound(Bundle.LBL_ThemesDirectory()));
            return;
        }

        // for validation of theme name
        setExistingThemeNames();

        // create dialog
        InputLine descriptor = new InputLine(Bundle.LBL_ThemeDirectoryName(), Bundle.LBL_ThemeDirectoryNameTitle(getName()), NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.PLAIN_MESSAGE);
        descriptor.setValid(false);

        if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION) {
            // get theme directory name
            String themeName = descriptor.getInputText().trim();
            if (StringUtils.isEmpty(themeName)) {
                LOGGER.log(Level.WARNING, Bundle.LBL_NotFound(Bundle.LBL_ThemeName()));
                return;
            }

            // create new theme directory
            FileObject themeDirectory = createThemeDirectory(phpModule, themeName);
            if (themeDirectory == null) {
                LOGGER.log(Level.WARNING, Bundle.LBL_NotFound(Bundle.LBL_ThemeDirectory()));
                return;
            }

            // unzip
            try {
                unzip(getUrl(), FileUtil.toFile(themeDirectory));
            } catch (MalformedURLException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Get ZipEntryFilter.
     *
     * @return ZipEntryFilter
     */
    protected ZipEntryFilter getZipEntryFilter() {
        return new GithubZipEntryFilter();
    }

    @NbBundle.Messages("LBL_CreatingTheme=Creating theme")
    protected void unzip(final String url, final File themeDirectory) throws MalformedURLException, IOException {
        ZipEntryFilter filter = getZipEntryFilter();
        final ZipEntryFilter entryFilter;
        if (filter == null) {
            entryFilter = new GithubZipEntryFilter();
        } else {
            entryFilter = filter;
        }

        // display progress bar
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.LBL_CreatingTheme(), new Cancellable() {
                    @Override
                    public boolean cancel() {
                        return true;
                    }
                });
                try {
                    handle.start();
                    WPFileUtils.unzip(url, themeDirectory, entryFilter);
                } catch (MalformedURLException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    handle.finish();
                }
            }
        });

    }

    /**
     * Create theme directory.
     *
     * @param phpModule
     * @param themeName theme name
     * @return new theme directory
     */
    protected FileObject createThemeDirectory(PhpModule phpModule, String themeName) {
        FileObject themeDirectory = null;
        try {
            themeDirectory = themesDirectory.createFolder(themeName);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return themeDirectory;
    }

    /**
     * Set existing theme names.
     */
    private void setExistingThemeNames() {
        existingThemeNames = new HashSet<String>();
        if (themesDirectory != null) {
            for (FileObject child : themesDirectory.getChildren()) {
                if (child.isFolder()) {
                    existingThemeNames.add(child.getName());
                }
            }
        }
    }

    /**
     * Get existing theme names.
     *
     * @return theme names
     */
    public Set<String> getExistingThemeNames() {
        if (existingThemeNames == null) {
            return Collections.emptySet();
        }
        return existingThemeNames;
    }

    //~ inner classes
    private class InputLine extends NotifyDescriptor.InputLine {

        public InputLine(String text, String title) {
            super(text, title);
            addDocumentListener();
        }

        public InputLine(String text, String title, int optionType, int messageType) {
            super(text, title, optionType, messageType);
            addDocumentListener();
        }

        private void addDocumentListener() {
            Document document = textField.getDocument();
            document.addDocumentListener(new DocumentListenerImpl(this));
        }
    }

    private class DocumentListenerImpl implements DocumentListener {

        private final InputLine inputLine;
        private final Set<String> existingThemes;

        public DocumentListenerImpl(InputLine inputLine) {
            this.inputLine = inputLine;
            existingThemes = getExistingThemeNames();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            fire();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            fire();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            fire();
        }

        private void fire() {
            String input = inputLine.getInputText().trim();
            if (input.isEmpty() || existingThemes.contains(input)) {
                inputLine.setValid(false);
            } else {
                inputLine.setValid(true);
            }
        }
    }
}
