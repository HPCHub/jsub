/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.ant.Property;


/**
 * {@link java.util.Properties} extension class with duplicate keys support
 * and commented values as alternatives for non commented.
 * @author zeleniy
 */
public class Properties {


    private static Logger _logger = Logger.getLogger(Properties.class);
    private List<Property> _properties;


    public Properties(Map<String, String> properties) {

        _properties = new ArrayList<Property>();
        for (Map.Entry<String, String> property : properties.entrySet()) {
            _properties.add(new Property(property.getKey(), property.getValue()));
        }
    }


    public Property getByName(String name, boolean isTrim) {

        for (Property property : _properties) {
            if (isTrim) {
                if (Property.getClearName(name).equals(property.getClearName())) {
                    return property;
                }
            } else {
                if (name.equals(property.getName())) {
                    return property;
                }
            }
        }

        return null;
    }


    public Property getLikeName(String[] names, boolean isTrim) {

        for (String name : names) {
            Property property = getLikeName(name, isTrim);
            if (property != null) {
                return property;
            }
        }

        return null;
    }


    public Property getLikeName(String name, boolean isTrim) {

        String prop;
        for (Property property : _properties) {
            if (isTrim) {
                name = Property.getClearName(name);
                prop = property.getClearName();
            } else {
                prop = property.getName();
            }

            Pattern pattern = Pattern.compile(String.format(".*%s.*", name));
            Matcher matcher = pattern.matcher(prop);
            if (matcher.matches()) {
                return property;
            }
        }

        return null;
    }


    public Properties(List<Property> properties) {

        if (properties == null) {
            properties = new ArrayList<Property>();
        }

        _properties = properties;
    }


    public static List<Property> getProperties(File file) {

        List<Property> result = new ArrayList<Property>();

        try {
            java.util.Properties properties = new java.util.Properties();
            properties.load(new FileInputStream(file));
            Enumeration<Object> enumeration = properties.keys();

            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String value = properties.getProperty(name);
                result.add(new Property(name, value));
            }
        } catch (IOException e) {
            _logger.error(
                String.format("Cannot load \"%s\" file: [%s] %s", file.getPath(), e.getClass().getName(), e.getMessage())
            );
        }

        return result;
    }


    public void load(InputStream inStream) throws IOException {

        throw new IOException("Not implemented");
    }


    public void load(Reader reader) throws IOException {

        throw new IOException("Not implemented");
    }


    public static List<Property> getInputFiles(List<Property> properties) {

        List<Property> result = new ArrayList<Property>();
        for (Property property : properties) {
            if (Property.isInput(property)) {
                result.add(property);
            }
        }

        return result;
    }


    public static Set<Property> getInputFiles(Set<Property> properties) {

        Set<Property> result = new TreeSet<Property>();
        for (Property property : properties) {
            if (Property.isInput(property)) {
                result.add(property);
            }
        }

        return result;
    }


    public List<String> getNames() {

        List<String> names = new ArrayList<String>();
        for (Property property : _properties) {
            names.add(property.getName());
        }

        return names;
    }


    public boolean containsInGeneral(Property property) {

        String name = property.getName();
        if (_properties.size() == 0) {
            return false;
        }

        Property firstProperty = _properties.get(0);
        if (Property.isInput(property) != Property.isInput(firstProperty)) {
            name = property.convert().getName();
        }

        return getNames().contains(name);
    }
}