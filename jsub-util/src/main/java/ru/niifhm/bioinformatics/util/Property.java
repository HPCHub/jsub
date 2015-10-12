/**
 * 
 */
package ru.niifhm.bioinformatics.util;

import java.util.Map;


/**
 * Simple property class.
 * It has name and value.
 * @author zeleniy
 */
public class Property implements Comparable<Property> {


    /**
     * Property name.
     */
    private String _name;
    /**
     * Property value.
     */
    private String _value;


    /**
     * @param name
     * @param value
     */
    public Property(String name, String value) {

        _name = name;
        _value = value;
    }


    public Property(Map.Entry<String, String> entry) {

        _name = entry.getKey();
        _value = entry.getValue();
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Property property) {

        if (hashCode() == property.hashCode()) {
            return 0;
        }

        if (hashCode() > property.hashCode()) {
            return 1;
        } else {
            return -1;
        }
    }


    /**
     * Get property name.
     * @return
     */
    public String getName() {

        return _name;
    }


    /**
     * Get property value.
     * @return
     */
    public String getValue() {

        return _value;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {

        if (object == null) {
            return false;
        }

        if (! (object instanceof Property)) {
            return false;
        }

        Property property = (Property) object;

        return getName().equals(property.getName()) && 
            getValue().equals(property.getValue());
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {

        return _name.hashCode() + _value.hashCode();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        return new StringBuilder("[ ")
            .append(_name)
            .append(" : ")
            .append(_value)
            .append(" ]")
        .toString();
    }
}