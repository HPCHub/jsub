/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.template;


import java.util.List;
import java.util.Map;
import ru.niifhm.bioinformatics.jsub.ant.Property;


/**
 * @author zeleniy
 */
public interface Template {


    /**
     * Is no more placeholders within template text.
     * @return
     */
    public boolean isEmpty();


    /**
     * Get parsed text.
     * @return
     */
    public String getText();


    /**
     * Get template placeholders list.
     * @return
     */
    public List<String> getPlaceholders();


    /**
     * Set placeholders values.
     * @param values
     */
    public void setPlaceholders(Map<String, String> values);


    /**
     * @param properties
     */
    public Template setPlaceholders(List<Property> values);


    /**
     * Set single placeholder.
     * @param placeholder
     * @param value
     * @return
     */
    public void setPlaceholder(String placeholder, String value);


    /**
     * Replace all placeholders to values.
     */
    public String replace();
}