/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.configuration;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.niifhm.bioinformatics.util.StringPool;


/**
 * Loads plain xml files.
 * @author zeleniy
 */
public class XmlConfigurationLoader extends DefaultConfigurationLoader {


    /**
     * Class logger.
     */
    private static Logger _log = Logger.getLogger(XmlConfigurationLoader.class);
    private InputStream   _input;


    /**
     * @param file
     */
    public XmlConfigurationLoader(String pathname) {

        super(pathname);

        try {
            _input = new FileInputStream(new File(pathname));
        } catch (Exception e) {
            _log.error(String.format(
                "Cannot initialize config with file \"%s\" [%s] %s",
                pathname,
                e.getClass().getName(),
                e.getMessage()
            ));
        }
    }


    /**
     * @param file
     */
    public XmlConfigurationLoader(File file) {

        super(file);

        try {
            _input = new FileInputStream(file);
        } catch (Exception e) {
            _log.error(String.format(
                "Cannot initialize config with file \"%s\" [%s] %s",
                file.getPath(),
                e.getClass().getName(),
                e.getMessage()
            ));
        }
    }


    /**
     * @param file
     */
    public XmlConfigurationLoader(InputStream input) {

        super(new File(StringPool.EMPTY));
        _input = input;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.configuration.DefaultConfigurationLoader#_loadConfiguration()
     */
    protected void _loadConfiguration() {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document _document = builder.parse(_input);
            NodeList tools = _document.getDocumentElement().getChildNodes();
            for (int i = 0; i < tools.getLength(); i ++) {
                Node tool = tools.item(i);
                if (! tool.hasAttributes()) {
                    continue;
                }

                NamedNodeMap attributes = tool.getAttributes();
                for (int j = 0; j < attributes.getLength(); j ++) {
                    Node attribute = attributes.item(j);
                    _configuration.put(tool.getNodeName() + "." + attribute.getNodeName(), attribute.getNodeValue());
                }
            }
        } catch (Exception e) {
            _log.error(String.format("Cannot load configuration from %s: %s", _file.getName(), e.getMessage()));
        }
    }


    protected Map<String, String> _getPlainConfiguration() {

        return _configuration;
    }


    protected Map<String, Map<String, String>> _getHierarchicalConfiguration() {

        Map<String, Map<String, String>> hierarchicalConfiguration = new HashMap<String, Map<String, String>>();
        for (Map.Entry<String, String> property : _configuration.entrySet()) {
            String key = property.getKey();
            int dotIndex = key.indexOf(".");
            String section = key.substring(0, dotIndex);
            String subsection = key.substring(dotIndex + 1);

            if (hierarchicalConfiguration.containsKey(section)) {
                Map<String, String> subConfiguration = hierarchicalConfiguration.get(section);
                subConfiguration.put(subsection, property.getValue());
            } else {
                Map<String, String> subConfiguration = new HashMap<String, String>();
                subConfiguration.put(subsection, property.getValue());
                hierarchicalConfiguration.put(section, subConfiguration);
            }
        }

        return hierarchicalConfiguration;
    }
}