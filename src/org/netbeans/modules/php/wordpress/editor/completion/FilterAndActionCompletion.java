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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.netbeans.spi.editor.completion.support.AsyncCompletionQuery;
import org.netbeans.spi.editor.completion.support.AsyncCompletionTask;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = "text/x-php5", service = CompletionProvider.class)
public class FilterAndActionCompletion extends WordPressCompletionProvider {

    private static final List<String> filters = new ArrayList<String>();
    private static final List<String> actions = new ArrayList<String>();
    private static final Logger LOGGER = Logger.getLogger(FilterAndActionCompletion.class.getName());
    private static final String FILTER_CODE_COMPLETION_FILE = "org-netbeans-modules-php-wordpress/wp-filters-list.txt"; // NOI18N
    private static final String ACTION_CODE_COMPLETION_FILE = "org-netbeans-modules-php-wordpress/wp-actions-list.txt"; // NOI18N
    private int argCount;
    private boolean isFilter = false;
    private boolean isAction = false;

    static {
        FileObject filterFile = FileUtil.getConfigFile(FILTER_CODE_COMPLETION_FILE);
        FileObject actionFile = FileUtil.getConfigFile(ACTION_CODE_COMPLETION_FILE);
        try {
            List<String> lines = filterFile.asLines();
            for (String line : lines) {
                filters.add(line);
            }
            lines = actionFile.asLines();
            for (String line : lines) {
                actions.add(line);
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
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
                    List<String> completions = getCodeCompletionList(doc);

                    if (isAction || isFilter) {
                        for (String completion : completions) {
                            if (!completion.isEmpty()
                                    && completion.startsWith(filter)) {
                                if (isAction) {
                                    completionResultSet.addItem(new ActionCompletionItem(completion, startOffset, removeLength));
                                } else if (isFilter) {
                                    completionResultSet.addItem(new FilterCompletionItem(completion, startOffset, removeLength));
                                }
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

    private List<String> getCodeCompletionList(Document doc) {
        List<String> list = new ArrayList<String>();
        if (argCount == 1) {
            if (isFilter) {
                list = filters;
            } else if (isAction) {
                list = actions;
            }
        } else if (argCount == 2) {
        }
        return list;
    }
}
