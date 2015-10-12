/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;
import ru.niifhm.bioinformatics.util.StringPool;


/**
 * Устаревший класс. Частично файл предоставляет доступ к конфигурационным опциям
 * jsub хранящихся в файлах вида jsub.*.properties. Частично файл предоставляет 
 * доступ к опциям, с которыми был запущен jsub. Класс нуждается в ревизии использования
 * методов, переносе их в {@link XConfig}, {@link Layout} и возможно переносе его
 * в проект jsub-cli. В целом, я уже не могу вспомнить зачем он, каковы его функции
 * и нужен ли он.
 * @deprecated
 * @author zeleniy
 */
public class Config extends HashMap<String, String> {


    public static final String     GRID_REPORTING_FILE        = "grid.reporting.file";
    public static final String     JSUB_WEB_PROJECT_DIR       = "web.project.dir";
    public static final String     PROPERTY_PROJECT_DIR       = "dir";
    public static final String     PROPERTY_PROJECT_TYPE      = "type";
    public static final String     PROPERTY_PROJECT_NAME      = "name";
    public static final String     PROPERTY_PROJECT_TAG       = "tag";
    public static final String     PROPERTY_BUILD_DIR         = "build-dir";
    public static final String     PROPERTY_CLEAR_INPUT       = "clear-input";
    public static final String     PROPERTY_PROPERTIES        = "properties";
    public static final String     PROPERTY_SKIP_LIST         = "skip-list";
    public static final String     PROPERTY_START_PHASE       = "start";
    public static final String     FILE_LOG4G_XML             = "log4j.xml";
    public static final String     MODE_DEBUG                 = "debug";
    public static final String     FLAG_LOG_STDOUT            = "log-stdout";
    public static final String     FLAG_FORCE                 = "force";
    public static final String     PROPERTY_PROJECT_BUILD_DIR = "project.build.dir";
    public static final String     FLAG_SKIP_COPY_PHASE       = "skip-copy";
    public static final String     FLAG_SKIP_TEST_PHASE       = "skip-test";
    public static final String     PROPERTY_COMMAND           = "command";
    public static final String     CONVEYOR_DIR               = "conveyor.dir";
    // TODO: it must be only in config file
    public static final String     ANT_SCRIPT_DIR             = "ant.script.dir";
    public static final String     SHELL_SCRIPT_DIR           = "shell.script.dir";
    public static final String     PYTHON_SCRIPT_DIR          = "python.script.dir";

    public static final String     LIBRARY_ANT_CONTRIB        = "ant-contrib.jar";
    public static final String     PROPERTY_MODE              = "mode";
    /**
     * Configuration class instance.
     */
    private static volatile Config _instance;
    /**
     * Class logger.
     */
    private static Logger          _log                       = Logger.getLogger(Config.class);


    /**
     * @return
     */
    public static Config getInstance() {

        if (_instance == null) {
            synchronized (Config.class) {
                if (_instance == null) {
                    _instance = new Config();
                }
            }
        }

        return _instance;
    }


    /**
     * 
     */
    private Config() {

        try {
            // load jsub properties
            Properties properties = new Properties();
            String file = getEnvironmentFileName(Layout.FILE_JSUB_PROPERTIES);
//            properties.load(new FileInputStream(new File(Layout.getInstance().getConfigFile(file))));
            properties.load(Layout.getInstance().getResourceAsInputStream(file));
            Enumeration<Object> enumeration = properties.keys();

            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                set(key, value);
                _log.debug(String.format("Load %s = %s from %s", key, value, file));
            }
        } catch (Exception e) {
            _log.fatal(String.format(
                "Cannot load application properties [%s]: %s", e.getClass().getName(), e.getMessage()
            ));
        }
    }


    public boolean isSkiped(String target) {

        String list = get(PROPERTY_SKIP_LIST);
        if (list == null) {
            return false;
        }

        return Arrays.asList(list.split(StringPool.COMMA))
        .contains(target);
    }


    /**
     * TODO: отстой. Как только список опций изменится, то...
     * Эта хрень должна быть в jsub-cli.
     * @return
     */
    public Map<String, String> getOptions() {

        Map<String, String> options = new HashMap<String, String>();

        options.put(FLAG_SKIP_COPY_PHASE, get(FLAG_SKIP_COPY_PHASE));
        options.put(FLAG_SKIP_TEST_PHASE, get(FLAG_SKIP_TEST_PHASE));
        options.put(FLAG_LOG_STDOUT, get(FLAG_LOG_STDOUT));
        options.put(FLAG_FORCE, get(FLAG_FORCE));
        options.put(MODE_DEBUG, get(MODE_DEBUG));
        options.put(PROPERTY_MODE, get(PROPERTY_COMMAND));
        options.put(PROPERTY_CLEAR_INPUT, get(PROPERTY_CLEAR_INPUT));
        options.put(PROPERTY_START_PHASE, get(PROPERTY_START_PHASE));
        options.put(PROPERTY_PROPERTIES, get(PROPERTY_PROPERTIES));
        options.put(PROPERTY_BUILD_DIR, get(PROPERTY_BUILD_DIR));

        return options;
    }


    /**
     * @param file
     * @return
     */
    public String getEnvironmentFileName(String file) {

        int index = file.lastIndexOf(".");
        String name = file.substring(0, index);
        String extension = file.substring(index);
        String mode = XConfig.getInstance().getProperty("applicationMode");

        return new StringBuilder(name)
            .append(".")
            .append(mode)
            .append(extension)
        .toString();
    }


    /**
     * @return
     */
    public String getLibraryDir() {

        StringBuilder builder = new StringBuilder(getJsubApplicationPath())
            .append("..")
            .append(File.separator)
            .append("..")
            .append(File.separator)
            .append("lib");

        // TODO: вынести эту херь в инициализацию.
        try {
            return new File(builder.toString()).getCanonicalPath();
        } catch (IOException e) {
            return "";
        }
    }


    /**
     * @param library
     * @return
     */
    public String getLibraryPathname(String library) {

        return new StringBuilder(getLibraryDir())
            .append(File.separator)
            .append(LIBRARY_ANT_CONTRIB)
            .toString();
    }


    /**
     * @return
     */
    public String getJsubApplicationPath() {

        return getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    }


    /**
     * @param key
     * @param defaultValue
     * @return
     */
    public String get(Object key, String defaultValue) {

        String value = super.get(key);
        if (value == null) {
            return defaultValue;
        }

        return value;
    }


    /**
     * @param key
     * @return
     */
    public List<String> getList(String key) {

        String value = get(key);
        if (value == null) {
            return new ArrayList<String>();
        } else {
            return Arrays.asList(value.split(StringPool.COMMA));
        }
    }


    public Map<String, String> getMap(String key) {

        String value = get(key);
        Map<String, String> result = new HashMap<String, String>();

        if (value == null) {
            return result;
        }

        String[] pairs = value.split(StringPool.COMMA);
        for (String pairString : pairs) {
            String[] pair = pairString.split(StringPool.EQUAL);
            result.put(pair[0], pair[1]);
        }

        return result;
    }


    public boolean is(String key) {

        String value = get(key);
        if (value == null) {
            return false;
        } else {
            return value.equals("true");
        }
    }


    /**
     * @param key
     * @param value
     */
    public void set(String key, String value) {

        put(key, value);
    }


    /**
     * @param key
     * @param value
     */
    public void set(String key, Boolean value) {

        put(key, value.toString());
    }
}