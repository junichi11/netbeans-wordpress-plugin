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
package org.netbeans.modules.php.wordpress.commands;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommand;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class WordPressCliCommandListXmlBuilder implements WordPressCliCommandListBuilder {

    private static final Logger LOGGER = Logger.getLogger(WordPressCliCommandListXmlBuilder.class.getName());
    private Document document = null;

    @Override
    public void build(List<FrameworkCommand> commands) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (ParserConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        }

        if (document == null) {
            LOGGER.log(Level.WARNING, "can't create xml document.");
            return;
        }
        Element commandsElement = document.createElement("commands"); // NOI18N
        document.appendChild(commandsElement);
        for (FrameworkCommand command : commands) {
            Element commandElement = document.createElement("command"); // NOI18N
            commandElement.setAttribute("name", StringUtils.implode(Arrays.asList(command.getCommands()), " ")); // NOI18N
            commandElement.setAttribute("displayname", command.getDisplayName()); // NOI18N

            // description
            Element descriptionElement = document.createElement("description"); // NOI18N
            Text descriptionText = document.createCDATASection(command.getDescription());
            descriptionElement.appendChild(descriptionText);
            commandElement.appendChild(descriptionElement);

            // help
            Element helpElement = document.createElement("help"); // NOI18N
            Text helpText = document.createCDATASection(command.getHelp());
            helpElement.appendChild(helpText);
            commandElement.appendChild(helpElement);

            commandsElement.appendChild(commandElement);
        }
    }

    @Override
    public String asText() {
        if (document == null) {
            return null;
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            StringWriter stringWriter = new StringWriter();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.toString();
        } catch (TransformerConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (TransformerException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

}
