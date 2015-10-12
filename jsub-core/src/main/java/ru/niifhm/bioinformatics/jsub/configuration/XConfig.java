/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.configuration;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Layout;
import com.thoughtworks.xstream.XStream;


/**
 * Класс предоставляющий доступ к конфигурационныи опциям xconfig.xml.
 * Класс построен на основе библиотеки <a href="http://xstream.codehaus.org/">xstream</a>.
 * @author zeleniy
 */
public class XConfig {


    /**
     * Class instance.
     */
    private static XConfig            _instance;
    /**
     * Engines list.
     */
    private Map<String, List<String>> _engines                = new HashMap<String, List<String>>();
    /**
     * Properties list.
     */
    private Map<String, String>       _properties             = new HashMap<String, String>();
    private Map<String, String>       _extensionsToProperties = new HashMap<String, String>();
    private Map<String, List<String>> _clearLists             = new HashMap<String, List<String>>();
    /**
     * Class logger.
     */
    private static Logger             _logger                 = Logger.getLogger(XConfig.class);


    public static XConfig getInstance() {

        if (_instance == null) {
            try {
                XStream xstream = new XStream();
                xstream.registerConverter(new XstreamConverter());
                xstream.alias("configuration", XConfig.class);
                xstream.aliasField("engines", XConfig.class, "_engines");

                _instance = (XConfig) xstream.fromXML(Layout.getInstance().getConfigAsInputStream("xconfig.xml"));
            } catch (FileNotFoundException e) {
                _logger
                    .error(String.format("Cannot initialize xconfig [%s] %s", e.getClass().getName(), e.getMessage()));
            }
        }

        return _instance;
    }


    public XConfig() {

    }


    public List<String> getClearList(String pipeline) {

        List<String> clearList =  _clearLists.get(pipeline);
        if (clearList == null) {
            clearList = new ArrayList<String>();
        }

        return clearList;
    }


    public Map<String, List<String>> getClearLists() {

        return _clearLists;
    }


    public Map<String, String> getExtensionsToProperties() {

        return _extensionsToProperties;
    }


    public Map<String, String> getProperties() {

        return _properties;
    }


    public String getProperty(String name) {

        return _properties.get(name);
    }


    public String getScriptDirectory(String extension) {

        String engine = getScriptEngine(extension);
        if (engine == null) {
            return null;
        }

        String string = Config.getInstance().get(Config.SHELL_SCRIPT_DIR);
        File directory = new File(new File(string).getParentFile(), engine);

        if (! directory.exists()) {
            return null;
        }

        return directory.getAbsolutePath();
    }


    public String getScriptEngine(String extension) {

        for (Map.Entry<String, List<String>> entry : _engines.entrySet()) {

            for (String ext : entry.getValue()) {
                if (extension.equals(ext)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }


    public Map<String, List<String>> getEngines() {

        return _engines;
    }
}