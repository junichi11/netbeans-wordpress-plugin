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
package org.netbeans.modules.php.wordpress;

import java.util.HashMap;
import java.util.Map;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.wordpress.util.WPFileUtils;
import org.netbeans.modules.php.wordpress.util.WPUtils;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;

/**
 *
 * @author junichi11
 */
public final class WordPressVersion {

    private final String versionNumber;
    private final FileObject versionFile;
    private int major;
    private int minor;
    private int revision;
    private String developmentSuffix; // e.g. RC1, beta, ...
    private static final Map<PhpModule, WordPressVersion> VERSIONS = new HashMap<PhpModule, WordPressVersion>();

    public static WordPressVersion create(PhpModule phpModule) {
        WordPressVersion version = VERSIONS.get(phpModule);
        if (version != null) {
            return version;
        }

        FileObject versionFile = WPFileUtils.getVersionFile(phpModule);
        if (versionFile == null) {
            return null;
        }

        String versionNumber = WPUtils.getVersion(versionFile);
        if (versionNumber == null) {
            return null;
        }

        version = new WordPressVersion(versionNumber, versionFile);
        VERSIONS.put(phpModule, version);
        return version;
    }

    private WordPressVersion(String versionNumber, FileObject versionFile) {
        assert versionNumber != null;
        this.versionNumber = versionNumber;
        initVersion(versionNumber);
        this.versionFile = versionFile;
        this.versionFile.addFileChangeListener(new FileChangeAdapter() {

            @Override
            public void fileChanged(FileEvent fe) {
                FileObject versionFile = fe.getFile();
                initVersion(WPUtils.getVersion(versionFile));
            }
        });
    }

    private boolean initVersion(String versionNumber) throws NumberFormatException {
        String[] splitVersion = splitVersion(versionNumber);
        int length = splitVersion.length;
        this.major = -1;
        this.minor = -1;
        this.revision = -1;
        this.developmentSuffix = ""; // NOI18N
        if (length <= 1) {
            return true;
        }
        if (length >= 2) {
            this.major = Integer.parseInt(splitVersion[0]);
            this.minor = Integer.parseInt(splitVersion[1]);
        }
        if (length >= 3) {
            int revisionLength = splitVersion[2].length();
            if (revisionLength == 1) {
                this.revision = Integer.parseInt(splitVersion[2]);
            } else {
                this.developmentSuffix = splitVersion[2];
            }
        }
        if (length >= 4) {
            developmentSuffix = splitVersion[3];
        }
        return false;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRevision() {
        return revision;
    }

    public String getDevelopmentSuffix() {
        return developmentSuffix;
    }

    public boolean isMajor(int number) {
        return this.major == number;
    }

    public boolean isMinor(int number) {
        return this.minor == number;
    }

    public boolean isRevision(int number) {
        return this.revision == number;
    }

    public boolean isDevelopmentSuffix(String suffix) {
        return suffix.equals(this.developmentSuffix);
    }

    private String[] splitVersion(String versionNumber) {
        return versionNumber.split("[.-]"); // NOI18N
    }

}
