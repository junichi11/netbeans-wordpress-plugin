/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package org.netbeans.modules.php.wordpress.validators;

import java.util.Arrays;
import java.util.Collections;
import junit.framework.Assert;
import org.junit.Test;
import org.netbeans.modules.php.api.validation.ValidationResult;

/**
 *
 * @author junichi11
 */
public class WordPressDirectoryNameValidatorTest {

    public WordPressDirectoryNameValidatorTest() {
    }

    /**
     * Test of validateName method, of class WordPressDirectoryNameValidator.
     */
    @Test
    public void testValidateName() {
        // invalid
        ValidationResult result = new WordPressDirectoryNameValidator()
                .validateName(null)
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("@")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName(" ")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("/")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("?")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("<")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName(">")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("!")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("\"")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("#")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("$")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("%")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("&")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("'")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("(")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName(")")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("=")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("~")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("|")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("\\")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("^")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("`")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("{")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("}")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("[")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("]")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("+")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("*")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName(";")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName(":")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName(",")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("あ")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("日本語")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("ｱ")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("Ａ")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("test/")
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        // valid
        result = new WordPressDirectoryNameValidator()
                .validateName("a")
                .getResult();
        Assert.assertFalse(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("A")
                .getResult();
        Assert.assertFalse(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("-")
                .getResult();
        Assert.assertFalse(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName(".")
                .getResult();
        Assert.assertFalse(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("_")
                .getResult();
        Assert.assertFalse(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateName("test-child")
                .getResult();
        Assert.assertFalse(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

    }

    /**
     * Test of validateExistingName method, of class
     * WordPressDirectoryNameValidator.
     */
    @Test
    public void testValidateExistingName() {
        // invalid
        ValidationResult result = new WordPressDirectoryNameValidator()
                .validateExistingName("parent", Arrays.asList("parent", "parent2"))
                .getResult();
        Assert.assertTrue(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        // valid
        result = new WordPressDirectoryNameValidator()
                .validateExistingName("child", Arrays.asList("parent", "parent2"))
                .getResult();
        Assert.assertFalse(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());

        result = new WordPressDirectoryNameValidator()
                .validateExistingName("child", Collections.<String>emptyList())
                .getResult();
        Assert.assertFalse(result.hasWarnings());
        Assert.assertFalse(result.hasErrors());
    }
}
