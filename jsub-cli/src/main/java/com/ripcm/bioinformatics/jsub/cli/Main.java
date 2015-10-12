package com.ripcm.bioinformatics.jsub.cli;


import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Jsub;
import ru.niifhm.bioinformatics.jsub.Layout;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.command.CLICommand;
import ru.niifhm.bioinformatics.jsub.command.Command;


public class Main {


    private static CommandLine  _command;
    private static final String OPTION_HELP = "help";
    private static Logger       _log        = Logger.getLogger(Main.class);


    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            CommandLine commandLine = _parseCommandLine(args);
            if (args.length == 0 || _command.hasOption(OPTION_HELP)) {
                _printHelp(_getOptionsWithoutCommand());
                System.exit(Jsub.BUILD_SUCCESSFUL);
            }

            String commandName = commandLine.getOptionValue(Config.PROPERTY_COMMAND);
            if (commandName != null && ! _isCommandExists(commandName)) {
                throw new Exception(String.format("Command \"%s\" not found", commandName));
            }

            Jsub.configureLog4j(_command.hasOption(Config.FLAG_LOG_STDOUT));

            if (commandName == null) {
                _log.info("Start jsub without command");
            } else {
                _log.info(String.format("Start jsub with \"%s\" command", commandName));

                Config config = _initConfiguration();

                Jsub.getInstance();
                Pipeline pipeline = Pipeline.newInstance(
                    _command.getOptionValue(Config.PROPERTY_PROJECT_NAME),
                    _command.getOptionValue(Config.PROPERTY_PROJECT_TAG),
                    _command.getOptionValue(Config.PROPERTY_PROJECT_TYPE),
                    _command.getOptionValue(Config.PROPERTY_PROJECT_DIR, new File("").getAbsolutePath())
                );

                Command command = Command.factory(commandName);
                command.executeCommand();
            }
        } catch (Exception e) {
            String message = String.format("BUILD_FAILED [%s]: %s", e.getClass().getName(), e.getMessage());
            System.out.println(message);
            _log.fatal(message);
            if (_command.hasOption(Config.MODE_DEBUG)) {
                e.printStackTrace();
            }
            System.exit(Jsub.BUILD_FAILED);
        } finally {
            _log.info("Stop execution");
        }
    }


    /**
     * Init Configuration class.
     */
    private static Config _initConfiguration() {

        Config config = Config.getInstance();
        config.set(Config.FLAG_SKIP_COPY_PHASE, _command.hasOption(Config.FLAG_SKIP_COPY_PHASE));
        config.set(Config.FLAG_SKIP_TEST_PHASE, _command.hasOption(Config.FLAG_SKIP_TEST_PHASE));
        config.set(Config.FLAG_LOG_STDOUT, _command.hasOption(Config.FLAG_LOG_STDOUT));
        config.set(Config.FLAG_FORCE, _command.hasOption(Config.FLAG_FORCE));
        config.set(Config.MODE_DEBUG, _command.hasOption(Config.MODE_DEBUG));
        config.set(Config.PROPERTY_MODE, _command.getOptionValue(Config.PROPERTY_COMMAND));
        config.set(Config.PROPERTY_CLEAR_INPUT, _command.getOptionValue(Config.PROPERTY_CLEAR_INPUT));
        config.set(Config.PROPERTY_SKIP_LIST, _command.getOptionValue(Config.PROPERTY_SKIP_LIST));
        config.set(Config.PROPERTY_START_PHASE, _command.getOptionValue(Config.PROPERTY_START_PHASE));
        config.set(Config.PROPERTY_PROPERTIES, _command.getOptionValue(Config.PROPERTY_PROPERTIES));
        config.set(Config.PROPERTY_BUILD_DIR, _command.getOptionValue(Config.PROPERTY_BUILD_DIR));

        return config;
    }


    private static String _getWhitespaces(Command command) {

        // 26 - this is magic and nothing more.
        int count = 26 - command.getName().length();
        StringBuilder builder = new StringBuilder();
        while ( -- count != 0) {
            builder.append(" ");
        }

        return builder.toString();
    }


    /**
     * Print jsub cli usage.
     * @param options
     */
    private static void _printHelp(Options options) throws Exception {

        PrintWriter writer = new PrintWriter(System.out);
        writer.println("usage: jsub <COMMAND> options");
        writer.println("commands list:");

        Class<?>[] packageClasses = Layout.getInstance().getPackageClasses("ru.niifhm.bioinformatics.jsub.command");
        for (Class<?> clazz : packageClasses) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            Command command = (Command) clazz.newInstance();
            if (! CLICommand.class.isInstance(command)) {
                continue;
            }
            writer.println(String.format("  %s%s%s", command.getName(), _getWhitespaces(command),
                command.getDescription()));
        }

        writer.println("options list:");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printOptions(
            writer,
            HelpFormatter.DEFAULT_WIDTH,
            options,
            HelpFormatter.DEFAULT_LEFT_PAD,
            HelpFormatter.DEFAULT_DESC_PAD
        );

        writer.flush();
    }


    /**
     * Is command exists?
     * @param command
     * @return
     */
    private static boolean _isCommandExists(String command) {

        try {
            Command.factory(command);
        } catch (Exception e) {
            return false;
        }

        return true;
    }


    /**
     * Is command?
     * @param command
     * @return
     */
    private static boolean _isCommand(String command) {

        Pattern pattern = Pattern.compile("^\\w+$");
        Matcher matcher = pattern.matcher(command);

        return matcher.matches();
    }


    /**
     * Get jsub input arguments.
     * @param args
     * @return
     */
    private static String[] _getArguments(String[] args) {

        if (args.length == 0) {
            return args;
        }

        String command = args[0];
        if (! _isCommand(command)) {
            return args;
        }

        String[] arguments = new String[args.length + 1];
        arguments[0] = "--" + Config.PROPERTY_COMMAND;
        System.arraycopy(args, 0, arguments, 1, args.length);

        return arguments;
    }


    /**
     * Parse input arguments.
     * @param args
     * @return
     * @throws ParseException
     */
    public static CommandLine _parseCommandLine(String[] args) throws ParseException {

        String[] arguments = _getArguments(args);
        _command = new GnuParser().parse(_getOptionsWithCommand(), arguments);

        return _command;
    }


    /**
     * Get options with "command" option.
     * @return
     */
    private static Options _getOptionsWithCommand() {

        Options options = _getOptionsWithoutCommand();
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_COMMAND)
            .withDescription("command to execute")
            .hasArg(true)
            .withArgName("COMMAND")
            .isRequired(false)
        .create());

        return options;
    }


    /**
     * Get options without "command" option.
     * @return
     */
    private static Options _getOptionsWithoutCommand() {

        Options options = new Options();
        options.addOption("h", OPTION_HELP, false, "print this message");
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_PROJECT_TYPE)
            .withDescription("project type")
            .hasArg(true)
            .withArgName("TYPE")
            .isRequired(false)
        .create("t"));
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_PROJECT_DIR)
            .withDescription("project directory. default is current")
            .hasArg(true)
            .withArgName("DIR")
            .isRequired(false)
        .create("d"));
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_PROJECT_NAME)
            .withDescription("project name")
            .hasArg(true)
            .withArgName("NAME")
            .isRequired(false)
        .create("n"));
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_PROJECT_TAG)
            .withDescription("project type group")
            .hasArg(true)
            .withArgName("TAG")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_BUILD_DIR)
            .withDescription("build directory")
            .hasArg(true)
            .withArgName("PATH")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_CLEAR_INPUT)
            .withDescription("comma separated list of targets clears input")
            .hasArg(true)
            .withArgName("LIST")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_SKIP_LIST)
            .withDescription("comma separated list of targets to skip")
            .hasArg(true)
            .withArgName("LIST")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_PROPERTIES)
            .withDescription("comma separated list of name=value pairs")
            .hasArg(true)
            .withArgName("LIST")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.PROPERTY_START_PHASE)
            .withDescription("scenario start phase")
            .hasArg(true)
            .withArgName("PHASE")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.MODE_DEBUG)
            .withDescription("enable debug mode")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.FLAG_SKIP_COPY_PHASE)
            .withDescription("use original input files, not copies")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.FLAG_SKIP_TEST_PHASE)
            .withDescription("skip test phase")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.FLAG_LOG_STDOUT)
            .withDescription("log to stdout")
            .isRequired(false)
        .create());
        options.addOption(OptionBuilder.withLongOpt(Config.FLAG_FORCE)
            .withDescription("override existent project")
            .isRequired(false)
        .create());

        return options;
    }
}