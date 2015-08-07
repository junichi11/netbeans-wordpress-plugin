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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.ElementQueryFactory;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.QuerySupportFactory;
import org.netbeans.modules.php.editor.api.elements.FunctionElement;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.wordpress.util.Charset;
import org.netbeans.modules.php.wordpress.util.DocUtils;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;
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
@MimeRegistration(mimeType = FileUtils.PHP_MIME_TYPE, service = CompletionProvider.class)
public final class FilterAndActionCompletion extends WordPressCompletionProvider {

    enum Type {

        Filter,
        Action
    }

    private static final Logger LOGGER = Logger.getLogger(FilterAndActionCompletion.class.getName());
    private static final String CUSTOM_FILTER_CODE_COMPLETION_XML = "nbproject/code-completion-filter.xml"; // NOI18N
    private static final String CUSTOM_ACTION_CODE_COMPLETION_XML = "nbproject/code-completion-action.xml"; // NOI18N
    private static final String DEFAULT_FILTER_CODE_COMPLETION_XML = "resources/code-completion-filter.xml"; // NOI18N
    private static final String DEFAULT_ACTION_CODE_COMPLETION_XML = "resources/code-completion-action.xml"; // NOI18N
    private int argCount;
    private boolean isFilter = false;
    private boolean isAction = false;
    private final List<WordPressCompletionItem> filterItems = new ArrayList<>();
    private final List<WordPressCompletionItem> actionItems = new ArrayList<>();
    private String currentInput;

    public FilterAndActionCompletion() {
        refresh();
    }

    @Override
    public CompletionTask createTask(int queryType, JTextComponent component, final PhpModule phpModule) {
        return new AsyncCompletionTask(new AsyncCompletionQuery() {
            @Override
            protected void query(CompletionResultSet completionResultSet, Document doc, int caretOffset) {
                try {
                    TokenSequence<PHPTokenId> ts = DocUtils.getTokenSequence(doc);
                    if (ts == null) {
                        return;
                    }
                    ts.move(caretOffset);
                    ts.moveNext();
                    Token<PHPTokenId> token = ts.token();
                    if (token.id() != PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
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
                        return;
                    }

                    // filter
                    int substrLength = caretOffset - startOffset + 1;
                    String filter = ""; // NOI18N
                    if (substrLength > 1) {
                        filter = caretInput.substring(1, substrLength);
                    }
                    currentInput = filter;

                    // set isAction and isFilter
                    List<WordPressCompletionItem> completions = getCodeCompletionList(phpModule);

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
                    completionResultSet.finish();
                }
            }
        }, component);
    }

    /**
     * Check whether function is add_filter
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

    private List<WordPressCompletionItem> getCodeCompletionList(PhpModule phpModule) {
        List<WordPressCompletionItem> list = new ArrayList<>();
        if (argCount == 1) {
            if (isFilter) {
                list = filterItems;
            } else if (isAction) {
                list = actionItems;
            }
        } else if (argCount == 2) {
            list = getFunctionsList(phpModule);
        }
        return list;
    }

    private List<WordPressCompletionItem> getFunctionsList(PhpModule phpModule) {
        List<WordPressCompletionItem> list = new ArrayList<>();
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        String rootPath = ""; // NOI18N
        if (sourceDirectory != null) {
            rootPath = sourceDirectory.getPath();
        }
        ElementQuery.Index indexQuery = ElementQueryFactory.createIndexQuery(QuerySupportFactory.get(phpModule.getSourceDirectory()));
        Set<FunctionElement> functionElements = indexQuery.getFunctions(NameKind.create(currentInput, QuerySupport.Kind.PREFIX));
        for (FunctionElement element : functionElements) {
            FileObject fileObject = element.getFileObject();
            String description = ""; // NOI18N
            if (fileObject != null) {
                String path = fileObject.getPath();
                if (path.matches("^" + rootPath + ".*$")) { // NOI18N
                    description = path.replace(rootPath, "") + "<br />"; // NOI18N
                } else {
                    description = "PHP function"; // NOI18N
                }
            }
            list.add(new WordPressCompletionItem(element.getName(), description));
        }

        return list;
    }

    public void refresh() {
        // read file for filter
        filterItems.clear();
        actionItems.clear();
        FileObject customFilterXml = null;
        FileObject customActionXml = null;
        PhpModule phpModule = PhpModule.Factory.inferPhpModule();

        // use custom file
        if (phpModule != null) {
            FileObject projectDirectory = phpModule.getProjectDirectory();
            customFilterXml = projectDirectory.getFileObject(CUSTOM_FILTER_CODE_COMPLETION_XML);
            customActionXml = projectDirectory.getFileObject(CUSTOM_ACTION_CODE_COMPLETION_XML);
        }
        refresh(customFilterXml, Type.Filter);
        refresh(customActionXml, Type.Action);
    }

    private void refresh(FileObject customXml, Type type) {
        try (InputStream inputStream = getInputStream(customXml, type)) {
            if (inputStream != null) {
                try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.UTF8))) {
                    parse(reader, type);
                }
            }
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, null, ex);
        }
    }

    private void parse(Reader reader, Type type) {
        switch (type) {
            case Filter:
                WordPressCodeCompletionParser.parse(reader, filterItems);
                break;
            case Action:
                WordPressCodeCompletionParser.parse(reader, actionItems);
                break;
            default:
                throw new AssertionError();
        }
    }

    private InputStream getInputStream(FileObject customXml, Type type) {
        InputStream inputStream = null;
        if (customXml == null) {
            inputStream = getDefaultInputStream(type);
        } else {
            try {
                inputStream = customXml.getInputStream();
            } catch (FileNotFoundException ex) {
                LOGGER.log(Level.WARNING, null, ex);
            }
        }
        return inputStream;
    }

    private InputStream getDefaultInputStream(Type type) {
        switch (type) {
            case Filter:
                return FilterAndActionCompletion.class.getResourceAsStream(DEFAULT_FILTER_CODE_COMPLETION_XML);
            case Action:
                return FilterAndActionCompletion.class.getResourceAsStream(DEFAULT_ACTION_CODE_COMPLETION_XML);
            default:
                return null;
        }
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
            } catch (SAXException | IOException ex) {
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
