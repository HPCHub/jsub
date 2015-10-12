package ru.niifhm.bioinformatics.jsub;


import java.io.File;
import java.util.Enumeration;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.xml.DOMConfigurator;


/**
 * Jsub application representation.
 * @author zeleniy
 */
public class Jsub {


    public static final int      BUILD_SUCCESSFUL = 0;
    public static final int      BUILD_FAILED     = 1;
    public static final String   COMMAND_QUEUE    = "queue";
    private static final String  APPENDER_CONSOLE = "console";
    private static final String  APPENDER_FILE    = "file";
    private static Logger        _logger             = Logger.getLogger(Jsub.class);
    private Pipeline             _project;
    /**
     * Jsub instance.
     */
    private static volatile Jsub _instance;


    /**
     * Get jsub instance.
     * @return
     */
    public static Jsub getInstance() {

        if (_instance == null) {
            synchronized (Jsub.class) {
                if (_instance == null) {
                    _instance = new Jsub();
                }
            }
        }

        return _instance;
    }


    /**
     * 
     */
    private Jsub() {

        _createApplicationLayout();
    }


    public Pipeline getProject() {

        return _project;
    }


    public void setProject(Pipeline project) {

        _project = project;
    }


    /**
     * @param file
     */
    public void setLogFilepath(String file) {

        FileAppender appender = (FileAppender) Logger.getRootLogger().getAppender(APPENDER_FILE);

        if (appender == null) {
            _logger.warn(String.format("File appender \"%s\" not found", APPENDER_FILE));
            return;
        }

        appender.setFile(file);
        appender.activateOptions();
    }


    /**
     * @param file
     */
    public void resetLogFilepath() {

        FileAppender appender = (FileAppender) Logger.getRootLogger().getAppender(APPENDER_FILE);

        if (appender == null) {
            _logger.warn(String.format("File appender \"%s\" not found", APPENDER_FILE));
            return;
        }

        appender.setFile(Layout.getInstance().getApplicationLogFile());
        appender.activateOptions();
    }


    /**
     * Create tmp and log directories.
     */
    private void _createApplicationLayout() {

        Layout layout = Layout.getInstance();

        File temporaryDirectory = new File(layout.getTmpDirectory());
        if (! temporaryDirectory.exists()) {
            boolean isCreated = new File(layout.getTmpDirectory()).mkdir();
            if (! isCreated) {
                _logger.error(String.format("Cannot create temporary directory \"%s\"", temporaryDirectory.getPath()));
            }
        }

        File logDirectory = new File(layout.getLogDirectory());
        if (! logDirectory.exists()) {
            boolean isCreated = new File(layout.getLogDirectory()).mkdir();
            if (! isCreated) {
                _logger.error(String.format("Cannot create log directory \"%s\"", logDirectory.getPath()));
            }
        }
    }


    /**
     * Configure log4j.
     */
    public static void configureLog4j(boolean isLogStdout) {

        Layout layout = Layout.getInstance();
        LogLog.setInternalDebugging(false);
        DOMConfigurator.configure(layout.getResource(Config.FILE_LOG4G_XML));

        Logger root = Logger.getRootLogger();
        Enumeration<?> appenders = root.getAllAppenders();
        while (appenders.hasMoreElements()) {
            Appender appender = (Appender) appenders.nextElement();
            if (appender.getName().equals(APPENDER_CONSOLE) && ! isLogStdout) {
                root.removeAppender(appender);
            } else if (appender.getName().equals(APPENDER_FILE)) {
                FileAppender fileAppender = (FileAppender) appender;
                fileAppender.setFile(layout.getApplicationLogFile());
                fileAppender.activateOptions();
            }
        }
    }
}