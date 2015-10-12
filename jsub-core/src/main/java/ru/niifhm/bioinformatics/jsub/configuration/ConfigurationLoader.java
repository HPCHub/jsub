/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.configuration;


import java.util.Map;


/**
 * @author zeleniy
 */
public interface ConfigurationLoader {


    public Map<String, String> getConfiguration();


    public Map<String, String> getPlainConfiguration();


    public Map<String, Map<String, String>> getHierarchicalConfiguration();
}