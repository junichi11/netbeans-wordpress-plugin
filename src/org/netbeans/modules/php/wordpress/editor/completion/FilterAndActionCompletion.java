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
package org.netbeans.modules.php.wordpress.editor.completion;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = "text/x-php5", service = CompletionProvider.class)
public class FilterAndActionCompletion extends WordPressCompletionProvider {

    private static final Logger LOGGER = Logger.getLogger(FilterAndActionCompletion.class.getName());
    private static final String CUSTOM_FILTER_CODE_COMPLETION_XML = "nbproject/code-completion-filter.xml"; // NOI18N
    private static final String CUSTOM_ACTION_CODE_COMPLETION_XML = "nbproject/code-completion-action.xml"; // NOI18N
    private static final String DEFAULT_FILTER_CODE_COMPLETION_XML = "org-netbeans-modules-php-wordpress/code-completion-filter.xml"; // NOI18N
    private static final String DEFAULT_ACTION_CODE_COMPLETION_XML = "org-netbeans-modules-php-wordpress/code-completion-action.xml"; // NOI18N
    private int argCount;
    private boolean isFilter = false;
    private boolean isAction = false;
    private List<WordPressCompletionItem> filterItems = new ArrayList<WordPressCompletionItem>();
    private List<WordPressCompletionItem> actionItems = new ArrayList<WordPressCompletionItem>();

    public FilterAndActionCompletion() {
        // read file for filter
        FileObject filterXml = null;
        FileObject actionXml = null;
        PhpModule phpModule = PhpModule.inferPhpModule();
        // use custom file
        if (phpModule != null) {
            FileObject projectDirectory = phpModule.getProjectDirectory();
            if (projectDirectory != null) {
                filterXml = projectDirectory.getFileObject(CUSTOM_FILTER_CODE_COMPLETION_XML);
                actionXml = projectDirectory.getFileObject(CUSTOM_ACTION_CODE_COMPLETION_XML);
            }
        }
        if (filterXml == null) {
            filterXml = FileUtil.getConfigFile(DEFAULT_FILTER_CODE_COMPLETION_XML);
        }
        if (actionXml == null) {
            actionXml = FileUtil.getConfigFile(DEFAULT_ACTION_CODE_COMPLETION_XML);
        }
        try {
            Reader filterReader = new BufferedReader(new InputStreamReader(filterXml.getInputStream()));
            WordPressCodeCompletionParser.parse(filterReader, filterItems);
            Reader actionReader = new BufferedReader(new InputStreamReader(actionXml.getInputStream()));
            WordPressCodeCompletionParser.parse(actionReader, actionItems);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    @Override
    public CompletionTask createTask(int queryType, JTextComponent component, final PhpModule phpModule) {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @Override
            protected void query(CompletionResultSet completionResultSet, Document doc, int caretOffset) {
                AbstractDocument ad = (AbstractDocument) doc;
                ad.readLock();
                TokenHierarchy hierarchy = TokenHierarchy.get(doc);
                try {
                    TokenSequence<PHPTokenId> ts = hierarchy.tokenSequence(PHPTokenId.language());
                    ts.move(caretOffset);
                    ts.moveNext();
                    Token<PHPTokenId> token = ts.token();
                    if (token.id() != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                        completionResultSet.finish();
                        return;
                    }
                    String caretInput = ts.token().text().toString();

                    int startOffset = ts.offset() + 1;
                    int removeLength = caretInput.length() - 2;
                    if (removeLength < 0) {
                        removeLength = 0;
                    }
                    int length = caretInput.length();

                    // check whether funciton is add_filter
                    if (!isValidCompletion(ts) || length < 2) {
                        completionResultSet.finish();
                        return;
                    }

                    // filter
                    int substrLength = caretOffset - startOffset + 1;
                    String filter = ""; // NOI18N
                    if (substrLength > 1) {
                        filter = caretInput.substring(1, substrLength);
                    }

                    // set isAction and isFilter
                    List<WordPressCompletionItem> completions = getCodeCompletionList();

                    if (isAction || isFilter) {
                        for (WordPressCompletionItem completion : completions) {
                            String text = completion.getText();
                            if (!text.isEmpty()
                                    && text.startsWith(filter)) {
                                completion.setOffset(startOffset, removeLength);
                                completionResultSet.addItem(completion);
                            }
                        }
                    }
                } finally {
                    ad.readUnlock();
                }

                completionResultSet.finish();
            }
        }, component);
    }

    /**
     * Check whether funciton is add_filter
     *
     * @param ts
     * @return true if add_filter, otherwise false
     */
    private boolean isValidCompletion(TokenSequence<PHPTokenId> ts) {
        argCount = 1;
        isFilter = false;
        isAction = false;
        while (ts.movePrevious()) {
            String function = ts.token().text().toString();
            if (ts.token().id() == PHPTokenId.PHP_STRING) {
                if (isFilter(function)) {
                    isFilter = true;
                    return true;
                }

                if (isAction(function)) {
                    isAction = true;
                    return true;
                }

            }

            if (function.contains(",")) { // NOI18N
                argCount++;
            }

            if (function.equals(";") // NOI18N
                    || function.endsWith(">") // NOI18N
                    || function.endsWith("\n")) { // NOI18N
                break;
            }
        }
        return false;
    }

    private boolean isFilter(String name) {
        return name.equals("add_filter") || name.equals("remove_filter"); // NOI18N
    }

    private boolean isAction(String name) {
        return name.equals("add_action") || name.equals("remove_action"); // NOI18N
    }

    private List<String> getCurrentFunctions(Document doc) {
        // TODO implement
        List<String> list = new ArrayList<String>();
        return list;
    }

    private List<WordPressCompletionItem> getCodeCompletionList() {
        List<WordPressCompletionItem> list = new ArrayList<WordPressCompletionItem>();
        if (argCount == 1) {
            if (isFilter) {
                list = filterItems;
            } else if (isAction) {
                list = actionItems;
            }
        } else if (argCount == 2) {
        }
        return list;
    }

    private static class WordPressCodeCompletionParser extends DefaultHandler {

        private static final String NAME = "name"; // NOI18N
        private static final String DESCRIPTION = "description"; // NOI18N
        private static final String FILTER = "filter"; // NOI18N
        private static final String ACTION = "action"; // NOI18N
        private static final String CATEGORY = "category"; // NOI18N
        private final XMLReader xmlReader;
        private final List<WordPressCompletionItem> items;
        private String currentName = null;
        private String currentDescription = null;
        private String currentCategory = null;
        boolean isName = false;
        boolean isDescription = false;

        public WordPressCodeCompletionParser(List<WordPressCompletionItem> items) throws SAXException {
            this.xmlReader = FileUtils.createXmlReader();
            this.items = items;
            xmlReader.setContentHandler(this);
        }

        public static void parse(Reader reader, List<WordPressCompletionItem> items) {
            try {
                WordPressCodeCompletionParser parser = new WordPressCodeCompletionParser(items);
                parser.xmlReader.parse(new InputSource(reader));
            } catch (SAXException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (FILTER.equals(qName) || ACTION.equals(qName)) {
                currentCategory = "<b>" + attributes.getValue(CATEGORY) + "</b><br/>"; // NOI18N
            } else if (NAME.equals(qName)) {
                isName = true;
            } else if (DESCRIPTION.equals(qName)) {
                isDescription = true;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (NAME.equals(qName)) {
                isName = false;
            } else if (DESCRIPTION.equals(qName)) {
                isDescription = false;
            } else if (FILTER.equals(qName)) {
                items.add(new FilterCompletionItem(currentName, currentCategory + currentDescription));
                resetCurrentValues();
            } else if (ACTION.equals(qName)) {
                items.add(new ActionCompletionItem(currentName, currentCategory + currentDescription));
                resetCurrentValues();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isName) {
                currentName = new String(ch, start, length);
            } else if (isDescription) {
                currentDescription = new String(ch, start, length);
            }
        }

        private void resetCurrentValues() {
            currentName = ""; // NOI18N
            currentDescription = ""; // NOI18N
            currentCategory = ""; // NOI18N
        }
    }
}
