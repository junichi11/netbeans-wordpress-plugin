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
package org.netbeans.modules.php.wordpress.validators;

import org.netbeans.api.annotations.common.NonNull;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.api.validation.ValidationResult;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author junichi11
 */
public final class WordPressCustomizerValidator {

    private final ValidationResult result = new ValidationResult();

    @NbBundle.Messages({
        "# {0} - directory name",
        "WordPressCustomizerValidator.wordpress.dir.invalid=Existing {0} directory name must be set.",
        "WordPressCustomizerValidator.wordpress.source.dir.invalid=Project might be broken...",
        "WordPressCustomizerValidator.wordpress.content.name.contains.shash=The name must not contain slash."
    })
    public WordPressCustomizerValidator validateWpContent(@NonNull PhpModule phpModule, FileObject wordPressRoot, String name, FileObject wpContentDirectory) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            result.addWarning(new ValidationResult.Message("wordpress.dir", Bundle.WordPressCustomizerValidator_wordpress_source_dir_invalid())); // NOI18N
            return this;
        }

        if (wordPressRoot == null) {
            result.addWarning(new ValidationResult.Message("wordpress.dir", Bundle.WordPressCustomizerValidator_wordpress_dir_invalid("WordPress Root"))); // NOI18N
            return this;
        }

        if (wpContentDirectory == null) {
            FileObject wpContent = wordPressRoot.getFileObject(name);
            if (wpContent == null
                    || !wpContent.isFolder()
                    || StringUtils.isEmpty(name)) {
                result.addWarning(new ValidationResult.Message("wordpress.content.name", Bundle.WordPressCustomizerValidator_wordpress_dir_invalid("content"))); // NOI18N
                return this;
            }

            if (name.contains("/")) { // NOI18N
                result.addWarning(new ValidationResult.Message("wordpress.content.name.slash", Bundle.WordPressCustomizerValidator_wordpress_content_name_contains_shash())); // NOI18N
                return this;
            }
        } else {

        }

        return this;
    }

    public WordPressCustomizerValidator validateWordPressRootDirectory(@NonNull PhpModule phpModule, @NonNull String path) {
        return validateDirectory(phpModule, path, "WordPress Root"); // NOI18N
    }

    public WordPressCustomizerValidator validatePluginsDirectory(@NonNull PhpModule phpModule, @NonNull String path) {
        return validateDirectory(phpModule, path, "plugins"); // NOI18N
    }

    public WordPressCustomizerValidator validateThemesDirectory(@NonNull PhpModule phpModule, @NonNull String path) {
        return validateDirectory(phpModule, path, "themes"); // NOI18N
    }

    public WordPressCustomizerValidator validateWpContentDirectory(@NonNull PhpModule phpModule, @NonNull String path) {
        if (path.isEmpty()) {
            return this;
        }
        return validateDirectory(phpModule, path, "wp-content"); // NOI18N
    }

    private WordPressCustomizerValidator validateDirectory(PhpModule phpModule, String path, String dirname) {
        FileObject sourceDirectory = phpModule.getSourceDirectory();
        if (sourceDirectory == null) {
            result.addWarning(new ValidationResult.Message("wordpress.dir", Bundle.WordPressCustomizerValidator_wordpress_source_dir_invalid())); // NOI18N
            return this;
        }

        FileObject targetDirectory = sourceDirectory.getFileObject(path);
        if (targetDirectory == null) {
            result.addWarning(new ValidationResult.Message("wordpress.dir", Bundle.WordPressCustomizerValidator_wordpress_dir_invalid(dirname)));
        }
        return this;
    }

    public ValidationResult getResult() {
        return result;
    }
}
