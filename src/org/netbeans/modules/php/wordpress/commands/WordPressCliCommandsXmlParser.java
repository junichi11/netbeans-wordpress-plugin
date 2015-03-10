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

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import org.netbeans.modules.php.api.util.FileUtils;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommand;
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
public class WordPressCliCommandsXmlParser extends DefaultHandler {

    private final List<FrameworkCommand> commands;
    private final XMLReader xmlReader;
    private String displayname;
    private String[] command;
    private Object content;
    private String description;
    private String help;

    public WordPressCliCommandsXmlParser(List<FrameworkCommand> commands) throws SAXException {
        assert commands != null;

        this.commands = commands;
        xmlReader = FileUtils.createXmlReader();
        xmlReader.setContentHandler(this);
    }

    public static void parse(Reader reader, List<FrameworkCommand> commands) {
        try {
            WordPressCliCommandsXmlParser parser = new WordPressCliCommandsXmlParser(commands);
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
        if ("command".equals(qName)) { // NOI18N
            command = attributes.getValue("name").split(" "); // NOI18N
            displayname = attributes.getValue("displayname"); // NOI18N
        } else if ("description".equals(qName)) { // NOI18N
            content = "description"; // NOI18N
        } else if ("help".equals(qName)) { // NOI18N
            content = "help"; // NOI18N
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("command".equals(qName)) {
            commands.add(new WordPressCliCommand(command, description, help));
            command = null;
            description = null;
            help = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("description".equals(content)) { // NOI18N
            description = new String(ch, start, length);
        } else if ("help".equals(content)) { // NOI18N
            help = new String(ch, start, length);
        }
    }

}
