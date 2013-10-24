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
package org.netbeans.modules.php.wordpress.wpapis;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.wordpress.ui.options.WordPressOptions;
import org.netbeans.modules.php.wordpress.util.Charset;

public class WordPressVersionCheckApi extends WordPressApi {

    private final String locale;
    private String version;
    private String download;
    private String phpVersion;
    private String mysqlVersion;
    private static final String VERSION_CHECK_17_API = "http://api.wordpress.org/core/version-check/1.7"; // NOI18N
    private static final String VERSION_CHECK_LOCALE_PARAM = "?locale=%s"; // NOI18N
    private static final Logger LOGGER = Logger.getLogger(WordPressVersionCheckApi.class.getName());

    public WordPressVersionCheckApi() {
        this.locale = WordPressOptions.getInstance().getWpLocale();
    }

    public WordPressVersionCheckApi(String locale) {
        this.locale = locale;
    }

    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(VERSION_CHECK_17_API);
        if (!StringUtils.isEmpty(locale)) {
            sb.append(String.format(VERSION_CHECK_LOCALE_PARAM, locale));
        }
        return sb.toString();
    }

    @Override
    public void parse() throws IOException {
        // get json
        InputStream inputStream = openStream();
        JSONObject jsonObject = (JSONObject) JSONValue.parse(new InputStreamReader(inputStream, Charset.UTF8));
        JSONArray offers = (JSONArray) jsonObject.get("offers"); // NOI18N

        // get version and locale
        String upgradeLocale;
        for (Object offer : offers) {
            JSONObject object = (JSONObject) offer;
            upgradeLocale = object.get("locale").toString(); // NOI18N
            version = object.get("version").toString(); // NOI18N
            download = object.get("download").toString(); // NOI18N
            phpVersion = object.get("php_version").toString(); // NOI18N
            mysqlVersion = object.get("mysql_version").toString(); // NOI18N
            if (StringUtils.isEmpty(this.locale)) {
                return;
            }
            if (upgradeLocale.equals(this.locale)) {
                return;
            }
        }

        // not found locale
        LOGGER.log(Level.WARNING, "Not found : specific locale data");
        reset();
    }

    private void reset() {
        this.version = null;
        this.download = null;
        this.phpVersion = null;
        this.mysqlVersion = null;
    }

    public String getLocale() {
        return locale;
    }

    public String getVersion() {
        return version;
    }

    public String getDownload() {
        return download;
    }

    public String getPhpVersion() {
        return phpVersion;
    }

    public String getMysqlVersion() {
        return mysqlVersion;
    }

}
