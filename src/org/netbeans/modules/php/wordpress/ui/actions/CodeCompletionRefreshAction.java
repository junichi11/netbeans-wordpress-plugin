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

import java.util.Collection;
import org.netbeans.api.editor.mimelookup.MimeLookup;
import org.netbeans.api.editor.mimelookup.MimePath;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.spi.framework.actions.BaseAction;
import org.netbeans.modules.php.wordpress.editor.completion.FilterAndActionCompletion;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public class CodeCompletionRefreshAction extends BaseAction {

    private static final long serialVersionUID = -1446444622440007833L;

    @NbBundle.Messages({
        "# {0} - name",
        "LBL_WordPressAction=WordPress Action: {0}"
    })
    @Override
    protected String getFullName() {
        return Bundle.LBL_WordPressAction(getPureName());
    }

    @NbBundle.Messages("LBL_ActionName=Code Completion Refresh")
    @Override
    protected String getPureName() {
        return Bundle.LBL_ActionName();
    }

    @Override
    protected void actionPerformed(PhpModule pm) {
        if (!WPUtils.isWP(pm)) {
            // called via shortcut
            return;
        }
        MimePath mimePath = MimePath.parse(FileUtils.PHP_MIME_TYPE);
        Collection<? extends CompletionProvider> providers = MimeLookup.getLookup(mimePath).lookupAll(CompletionProvider.class);
        for (CompletionProvider provider : providers) {
            if (provider instanceof FilterAndActionCompletion) {
                FilterAndActionCompletion completion = (FilterAndActionCompletion) provider;
                completion.refresh();
            }
        }
    }
}
