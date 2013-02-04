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
package org.netbeans.modules.php.wordpress.editor.navi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkProviderExt;
import org.netbeans.lib.editor.hyperlink.spi.HyperlinkType;
import org.netbeans.modules.csl.api.UiUtils;
import org.netbeans.modules.parsing.api.Source;
import org.netbeans.modules.parsing.spi.indexing.support.QuerySupport;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.editor.api.ElementQuery;
import org.netbeans.modules.php.editor.api.ElementQueryFactory;
import org.netbeans.modules.php.editor.api.NameKind;
import org.netbeans.modules.php.editor.api.QuerySupportFactory;
import org.netbeans.modules.php.editor.api.elements.FunctionElement;
import org.netbeans.modules.php.editor.lexer.LexUtilities;
import org.netbeans.modules.php.editor.lexer.PHPTokenId;
import org.netbeans.modules.php.editor.nav.NavUtils;
import org.netbeans.modules.php.wordpress.util.WPUtils;

/**
 *
 * @author junichi11
 */
@MimeRegistration(mimeType = "text/x-php5", service = HyperlinkProviderExt.class)
public class WordPressHyperlinkProviderExt implements HyperlinkProviderExt {

    private static final List<String> methods = Arrays.asList(
            "add_filter", // NOI18N
            "remove_filter", // NOI18N
            "add_action", // NOI18N
            "remove_action"); // NOI18N
    private String target;
    private int startOffset;
    private int endOffset;
    private PhpModule phpModule;
    private int argCount;

    @Override
    public Set<HyperlinkType> getSupportedHyperlinkTypes() {
        return Collections.singleton(HyperlinkType.GO_TO_DECLARATION);
    }

    @Override
    public boolean isHyperlinkPoint(Document doc, int offset, HyperlinkType ht) {
        Source source = Source.create(doc);
        phpModule = PhpModule.forFileObject(source.getFileObject());
        if (phpModule == null || !WPUtils.isWP(phpModule)) {
            return false;
        }

        AbstractDocument ad = (AbstractDocument) doc;
        ad.readLock();
        try {
            TokenSequence<PHPTokenId> ts = LexUtilities.getPHPTokenSequence(doc, offset);
            if (ts == null) {
                return false;
            }
            ts.move(offset);
            ts.moveNext();
            Token<PHPTokenId> token = ts.token();
            target = token.text().toString();
            PHPTokenId tokenId = token.id();
            int targetOffset = ts.offset();
            if (!isValid(ts)) {
                return false;
            }
            if (tokenId == PHPTokenId.PHP_CONSTANT_ENCAPSED_STRING) {
                int length = target.length();
                if (length > 2) {
                    startOffset = targetOffset;
                    endOffset = targetOffset + target.length();
                    target = NavUtils.dequote(target);
                    return true;
                }
            }
        } finally {
            ad.readUnlock();
        }
        target = ""; // NOI18N
        return false;
    }

    @Override
    public int[] getHyperlinkSpan(Document doc, int offset, HyperlinkType ht) {
        return new int[]{startOffset, endOffset};
    }

    @Override
    public void performClickAction(Document doc, int offset, HyperlinkType ht) {
        if (target != null && !target.isEmpty()) {
            open();
        }
    }

    @Override
    public String getTooltipText(Document doc, int offset, HyperlinkType ht) {
        return null;
    }

    /**
     * Check whether function name is for filter or action. Count argument.
     *
     * @param ts TokenSequence
     * @return true if function name is correct for argument, otherwise false
     */
    private boolean isValid(TokenSequence<PHPTokenId> ts) {
        int count = 1;
        argCount = 1;
        while (ts.movePrevious()) {
            Token<PHPTokenId> token = ts.token();
            String text = token.text().toString();
            if (text.contains(",")) { // NOI18N
                count++;
                continue;
            }
            if (text.contains("\n") || text.contains("{")) { // NOI18N
                break;
            }
            if (methods.contains(text)) {
                argCount = count;
                return true;
            }
        }
        return false;
    }

    /**
     * Move to specified function.
     */
    private void open() {
        ElementQuery.Index indexQuery = ElementQueryFactory.createIndexQuery(QuerySupportFactory.get(phpModule.getSourceDirectory()));
        Set<FunctionElement> functionElements = indexQuery.getFunctions(NameKind.create(target, QuerySupport.Kind.EXACT));
        for (FunctionElement element : functionElements) {
            if (element.getName().equals(target)) {
                UiUtils.open(element.getFileObject(), element.getOffset());
                return;
            }
        }
    }
}
