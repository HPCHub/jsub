/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.configuration;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * @author zeleniy
 */
public class XstreamConverter implements Converter {


    /*
     * (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
     */
    @Override
    public boolean canConvert(Class clazz) {

        return clazz.equals(XConfig.class);
    }


    /*
     * (non-Javadoc)
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
     * com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }


    /*
     * (non-Javadoc)
     * @see
     * com.thoughtworks.xstream.converters.Converter#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader,
     * com.thoughtworks.xstream.converters.UnmarshallingContext)
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

        XConfig xconfig = new XConfig();

        /*
         * Read properties
         */
        Map<String, String> properties = xconfig.getProperties();
        reader.moveDown(); // properties
        while (reader.hasMoreChildren()) {
            reader.moveDown(); // property
            String name = reader.getAttribute("name");
            String value = reader.getAttribute("value");

            properties.put(name, value);
            reader.moveUp();
        }
        reader.moveUp();

        /*
         * Read extension-property-map
         */
        Map<String, String> extensionsToProperties = xconfig.getExtensionsToProperties();
        reader.moveDown(); // extension-property-map
        while (reader.hasMoreChildren()) {
            reader.moveDown(); // extension
            String name = reader.getAttribute("name");
            String value = reader.getValue();

            extensionsToProperties.put(name, value);
            reader.moveUp();
        }
        reader.moveUp();

        /*
         * Read engines
         */
        Map<String, List<String>> engines = xconfig.getEngines();
        reader.moveDown(); // engines
        while (reader.hasMoreChildren()) {
            reader.moveDown(); // engine
            String engine = reader.getAttribute("name");
            List<String> extensions = new ArrayList<String>();
            while (reader.hasMoreChildren()) {
                reader.moveDown(); // extension
                extensions.add(reader.getValue());
                reader.moveUp();
            }

            engines.put(engine, extensions);
            reader.moveUp();
        }
        reader.moveUp();

        /*
         * Read pipelines
         */
        Map<String, List<String>> clearLists = xconfig.getClearLists();
        reader.moveDown(); // pipelines
        while (reader.hasMoreChildren()) {
            reader.moveDown(); // pipeline

            List<String> clearList = new ArrayList<String>();
            clearLists.put(reader.getNodeName(), clearList);

            reader.moveDown(); // clear-list
            while (reader.hasMoreChildren()) {
                reader.moveDown(); // target
                clearList.add(reader.getValue());
                reader.moveUp();
            }

            reader.moveUp();
            reader.moveUp();
        }

        return xconfig;
    }
}