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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import static java.util.logging.Level.WARNING;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.base.input.InputProcessor;
import org.netbeans.api.extexecution.base.input.InputProcessors;
import org.netbeans.api.extexecution.base.input.LineProcessor;
import org.netbeans.modules.php.api.executable.InvalidPhpExecutableException;
import org.netbeans.modules.php.api.executable.PhpExecutable;
import org.netbeans.modules.php.api.executable.PhpExecutableValidator;
import org.netbeans.modules.php.api.phpmodule.PhpModule;
import org.netbeans.modules.php.api.util.StringUtils;
import org.netbeans.modules.php.spi.framework.commands.FrameworkCommand;
import org.netbeans.modules.php.wordpress.modules.WordPressModule;
import org.netbeans.modules.php.wordpress.ui.options.WordPressOptions;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.InputOutput;

/**
 *
 * @author junichi11
 */
public final class WordPressCli {

    public static final String NAME = "wp"; // NOI18N
    public static final String LONG_NAME = "wp-cli.phar"; // NOI18N
    public static final String NAME_BAT = "wp.bat"; // NOI18N

    private final String wpCliPath;
    private boolean noReset = false;
    private static final Logger LOGGER = Logger.getLogger(WordPressCli.class.getName());

    // commands
    private static final String HELP_COMMAND = "help"; // NOI18N
    private static final String CLI_COMMAND = "cli"; // NOI18N
    private static final String VERSION_COMMAND = "version"; // NOI18N
    private static final String CORE_COMMAND = "core"; // NOI18N
    private static final String DOWNLOAD_COMMAND = "download"; // NOI18N
    private static final String UPDATE_COMMAND = "update"; // NOI18N
    private static final String UPDATE_DB_COMMAND = "update-db"; // NOI18N
    private static final String PLUGIN_COMMAND = "plugin"; // NOI18N
    private static final String THEME_COMMAND = "theme"; // NOI18N
    private static final String STATUS_COMMAND = "status"; // NOI18N

    // params
    private static final String HELP_PARAM = "--help"; // NOI18N
    public static final String LOCALE_PARAM = "--locale=%s"; // NOI18N
    public static final String VERSION_PARAM = "--version=%s"; // NOI18N
    public static final String ALL_PARAM = "--all"; // NOI18N

    // XXX default?
    private final List<String> DEFAULT_PARAMS = Collections.emptyList();
    private static final List<FrameworkCommand> COMMANDS_CACHE = new ArrayList<>();

    private WordPressCli(String wpCliPath) {
        this.wpCliPath = wpCliPath;
    }

    @NbBundle.Messages({
        "# {0} - error message",
        "WordPressCli.invalid.wpcli.script=<html>wp-cli is not valid.<br>({0})"
    })
    public static WordPressCli getDefault(boolean warn) throws InvalidPhpExecutableException {
        String wpCliPath = WordPressOptions.getInstance().getWpCliPath();
        String error = validate(wpCliPath);
        if (error == null) {
            return new WordPressCli(wpCliPath);
        }
        if (warn) {
            NotifyDescriptor.Message message = new NotifyDescriptor.Message(
                    Bundle.WordPressCli_invalid_wpcli_script(error),
                    NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(message);
        }
        throw new InvalidPhpExecutableException(error);
    }

    @NbBundle.Messages("WordPressCli.script.label=wp-cli")
    public static String validate(String path) {
        return PhpExecutableValidator.validateCommand(path, Bundle.WordPressCli_script_label());
    }

    /**
     * Run core download command. If you want to use specific language and
     * version, add --locale=[locale], --version=[version] to options.
     *
     * @param phpModule
     * @param options (--locale, --version)
     */
    public Future<Integer> download(PhpModule phpModule, List<String> options) {
        ArrayList<String> allCommands = new ArrayList<>(options.size() + 2);
        allCommands.add(CORE_COMMAND);
        allCommands.add(DOWNLOAD_COMMAND);
        allCommands.addAll(options);
        return runCommand(phpModule, allCommands);
    }

    /**
     * Core update.
     *
     * @param phpModule
     * @param options --zip, --version
     * @return
     */
    public Future<Integer> coreUpdate(PhpModule phpModule, List<String> options) {
        ArrayList<String> allCommands = new ArrayList<>(options.size() + 2);
        allCommands.add(CORE_COMMAND);
        allCommands.add(UPDATE_COMMAND);
        allCommands.addAll(options);
        return runCommand(phpModule, allCommands);
    }

    /**
     * Core update-db.
     *
     * @param phpModule
     * @return
     */
    public Future<Integer> coreUpdateDb(PhpModule phpModule) {
        ArrayList<String> allCommands = new ArrayList<>(2);
        allCommands.add(CORE_COMMAND);
        allCommands.add(UPDATE_DB_COMMAND);
        return runCommand(phpModule, allCommands);
    }

    /**
     * Plugin update.
     *
     * @param phpModule
     * @param options --all, --version --dry-run
     * @return
     */
    public Future<Integer> pluginUpdate(PhpModule phpModule, List<String> options) {
        ArrayList<String> allCommands = new ArrayList<>(options.size() + 2);
        allCommands.add(PLUGIN_COMMAND);
        allCommands.add(UPDATE_COMMAND);
        allCommands.addAll(options);
        return runCommand(phpModule, allCommands);
    }

    /**
     * Theme update.
     *
     * @param phpModule
     * @param options --all, --version --dry-run
     * @return
     */
    public Future<Integer> themeUpdate(PhpModule phpModule, List<String> options) {
        ArrayList<String> allCommands = new ArrayList<>(options.size() + 2);
        allCommands.add(THEME_COMMAND);
        allCommands.add(UPDATE_COMMAND);
        allCommands.addAll(options);
        return runCommand(phpModule, allCommands);
    }

    /**
     * Get wp-cli version.
     *
     * @return version
     */
    public String getVersion() {
        HelpLineProcessor helpLineProcessor = new HelpLineProcessor();
        Future<Integer> result = createExecutable()
                .additionalParameters(Arrays.asList(CLI_COMMAND, VERSION_COMMAND))
                .run(getSilentDescriptor(), getOutputProcessorFactory(helpLineProcessor));
        try {
            if (result != null) {
                result.get();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            LOGGER.log(WARNING, null, ex);
        }
        return helpLineProcessor.getHelp();
    }

    /**
     * Get plugin status.
     *
     * @return result
     */
    public List<String> getPluginStatus(PhpModule phpModule) {
        return getStatus(PLUGIN_COMMAND, phpModule);
    }

    /**
     * Get theme status.
     *
     * @return result
     */
    public List<String> getThemeStatus(PhpModule phpModule) {
        return getStatus(THEME_COMMAND, phpModule);
    }

    /**
     * Get status.
     *
     * @param command command name
     * @return result
     */
    private List<String> getStatus(String command, PhpModule phpModule) {
        HelpLineProcessor lineProcessor = new HelpLineProcessor();
        WordPressModule wpModule = WordPressModule.Factory.forPhpModule(phpModule);
        FileObject root = wpModule.getWordPressRootDirecotry();
        if (root == null) {
            lineProcessor.asLines();
        }

        Future<Integer> result = createExecutable()
                .workDir(FileUtil.toFile(root))
                .additionalParameters(Arrays.asList(command, STATUS_COMMAND))
                .run(getSilentDescriptor(), getOutputProcessorFactory(lineProcessor));
        try {
            if (result != null) {
                result.get();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return lineProcessor.asLines();
    }

    /**
     * Get help.
     *
     * @param commands
     * @return help for command
     */
    public String getHelp(List<String> commands) {
        List<String> params = new ArrayList<>(commands.size() + 1);
        params.addAll(commands);
        params.add(HELP_PARAM);

        HelpLineProcessor helpLineProcessor = new HelpLineProcessor();

        Future<Integer> result = createExecutable()
                .additionalParameters(params)
                .run(getSilentDescriptor(), getOutputProcessorFactory(helpLineProcessor));
        try {
            if (result != null) {
                result.get();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return helpLineProcessor.getHelp();
    }

    /**
     * Get commands from help. Also get subcommands, so, take a little time.
     *
     * @param isForce clear cache
     * @return commands
     */
    @NbBundle.Messages("WordPressCli.commands.empty=Please check whether config file and DB settings exist.")
    public List<FrameworkCommand> getCommands(boolean isForce) {
        if (!isForce && !COMMANDS_CACHE.isEmpty()) {
            return COMMANDS_CACHE;
        }
        COMMANDS_CACHE.clear();
        if (!isForce) {
            // exists xml?
            String commandList = WordPressOptions.getInstance().getWpCliCommandList();
            if (!StringUtils.isEmpty(commandList)) {
                try {
                    File temp = File.createTempFile("nb-wpcli-tmp", ".xml"); // NOI18N
                    try {
                        FileOutputStream outputStream = new FileOutputStream(temp);
                        PrintWriter pw = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8")); // NOI18N
                        try {
                            pw.println(commandList);
                        } finally {
                            pw.close();
                        }

                        // parse
                        FileInputStream fileInputStream = new FileInputStream(temp);
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8"); // NOI18N
                        WordPressCliCommandsXmlParser.parse(inputStreamReader, COMMANDS_CACHE);
                    } finally {
                        temp.deleteOnExit();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                if (!COMMANDS_CACHE.isEmpty()) {
                    return COMMANDS_CACHE;
                }
            }
        }

        // update
        updateCommands();

        return COMMANDS_CACHE;
    }

    public void updateCommands() {
        COMMANDS_CACHE.clear();
        getCommands(Collections.<String>emptyList(), COMMANDS_CACHE);
        if (COMMANDS_CACHE.isEmpty()) {
            NotifyDescriptor.Message message = new NotifyDescriptor.Message(Bundle.WordPressCli_commands_empty(), NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(message);
        } else {
            WordPressCliCommandListXmlBuilder builder = new WordPressCliCommandListXmlBuilder();
            builder.build(COMMANDS_CACHE);
            String commadlist = builder.asText();
            if (!StringUtils.isEmpty(commadlist)) {
                WordPressOptions.getInstance().setWpCliCommandList(commadlist);
            }
        }
    }

    // XXX get help later?
    private void getCommands(List<String> subcommands, List<FrameworkCommand> commands) {
        ArrayList<String> params = new ArrayList<>(subcommands.size() + 1);
        params.add(HELP_COMMAND);
        params.addAll(subcommands);
        HelpLineProcessor helpLineProcessor = new HelpLineProcessor();
        Future<Integer> result = createExecutable()
                .additionalParameters(params)
                .run(getSilentDescriptor(), getOutputProcessorFactory(helpLineProcessor));
        try {
            if (result != null) {
                result.get();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException ex) {
            LOGGER.log(WARNING, null, ex);
        }
        List<String> lines = helpLineProcessor.asLines();

        boolean isSubcommands = false;
        boolean isFirstEmpty = false;
        for (String line : lines) {
            if (isSubcommands) {
                if (StringUtils.isEmpty(line)) {
                    if (isFirstEmpty) {
                        break;
                    }
                    isFirstEmpty = true;
                    continue;
                }
                line = line.trim();
                line = line.replaceAll(" +", " "); // NOI18N
                int indexOf = line.indexOf(" "); // NOI18N
                if (indexOf == -1) {
                    continue;
                }

                // add command
                String subcommand = line.substring(0, indexOf);
                String description = line.substring(indexOf + 1);
                ArrayList<String> nextSubcommands = new ArrayList<>(subcommands.size() + 1);
                nextSubcommands.addAll(subcommands);
                nextSubcommands.add(subcommand);
                String help = getHelp(nextSubcommands);
                commands.add(new WordPressCliCommand(nextSubcommands.toArray(new String[]{}), description, help)); // NOI18N

                // recursive
                getCommands(nextSubcommands, commands);
            }

            if (line.toLowerCase().startsWith("subcommands")) { // NOI18N
                isSubcommands = true;
            }
        }
    }

    /**
     * Run Command.
     *
     * @param phpModule
     * @param parameters
     * @param postExecution
     */
    public void runCommand(PhpModule phpModule, List<String> parameters, Runnable postExecution) {
        PhpExecutable executable = getExecutable(phpModule);
        if (executable == null) {
            return;
        }
        executable.displayName(getDisplayName(phpModule, parameters.get(0)))
                .additionalParameters(getAllParameters(parameters))
                .run(getExecutionDescriptor(postExecution));
    }

    /**
     * Run Command.
     *
     * @param phpModule
     * @param parameters
     * @param postExecution
     */
    public Future<Integer> runCommand(PhpModule phpModule, List<String> parameters) {
        PhpExecutable executable = getExecutable(phpModule);
        if (executable == null) {
            return null;
        }
        return executable.displayName(getDisplayName(phpModule, StringUtils.implode(parameters, " "))) // NOI18N
                .additionalParameters(getAllParameters(parameters))
                .run(getExecutionDescriptor(null));
    }

    /**
     * Get PhpExecutable. Run php wp/wp-cli.phar [command]. Working directory is
     * source directory.
     *
     * @param phpModule
     * @return PhpExecutable
     */
    private PhpExecutable getExecutable(PhpModule phpModule) {
        WordPressModule wpModule = WordPressModule.Factory.forPhpModule(phpModule);
        FileObject wordPressRootDirectory = wpModule.getWordPressRootDirecotry();
        if (wordPressRootDirectory == null) {
            return null;
        }
        return createExecutable()
                .workDir(FileUtil.toFile(wordPressRootDirectory));
    }

    /**
     * Create PhpExecuttable.
     *
     * @return
     */
    private PhpExecutable createExecutable() {
        return new PhpExecutable(wpCliPath);
    }

    /**
     * Get execution descriptor.
     *
     * @param postExecution
     * @return execution descriptor
     */
    private ExecutionDescriptor getExecutionDescriptor(Runnable postExecution) {
        ExecutionDescriptor executionDescriptor = PhpExecutable.DEFAULT_EXECUTION_DESCRIPTOR
                .optionsPath(WordPressOptions.getOptionsPath());
        if (postExecution != null) {
            executionDescriptor = executionDescriptor.postExecution(postExecution);
        }
        return executionDescriptor;
    }

    /**
     * Get silent descriptor. Not show output window.
     *
     * @return descriptor
     */
    private ExecutionDescriptor getSilentDescriptor() {
        return new ExecutionDescriptor()
                .inputOutput(InputOutput.NULL);
    }

    /**
     * Get all parameters. Merge with default parameters.
     *
     * @param parameters
     * @return all parameters
     */
    private List<String> getAllParameters(List<String> parameters) {
        List<String> allParams = new ArrayList<>(DEFAULT_PARAMS.size() + parameters.size());
        allParams.addAll(DEFAULT_PARAMS);
        allParams.addAll(parameters);
        return allParams;
    }

    /**
     * Get InputProcessorFactory.
     *
     * @param lineProcessor
     * @return InputProcessorFactory
     */
    private ExecutionDescriptor.InputProcessorFactory2 getOutputProcessorFactory(final LineProcessor lineProcessor) {
        return new ExecutionDescriptor.InputProcessorFactory2() {
            @Override
            public InputProcessor newInputProcessor(InputProcessor defaultProcessor) {
                return InputProcessors.ansiStripping(InputProcessors.bridge(lineProcessor));
            }
        };
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "# {1} - command",
        "WordPressCli.command.title={0} ({1})"
    })
    private String getDisplayName(PhpModule phpModule, String command) {
        return Bundle.WordPressCli_command_title(phpModule.getDisplayName(), command);
    }

    private static class HelpLineProcessor implements LineProcessor {

        private final StringBuilder sb = new StringBuilder();
        private final List<String> list = new ArrayList<>();

        @Override
        public void processLine(String line) {
            list.add(line);
            sb.append(line).append("\n"); // NOI18N
        }

        @Override
        public void reset() {
        }

        @Override
        public void close() {
        }

        public String getHelp() {
            return sb.toString();
        }

        public List<String> asLines() {
            return list;
        }
    }
}
