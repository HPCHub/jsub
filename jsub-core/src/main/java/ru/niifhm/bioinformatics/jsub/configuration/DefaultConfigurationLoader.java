/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.configuration;


import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * @author zeleniy
 */
public abstract class DefaultConfigurationLoader implements ConfigurationLoader {


    private boolean                          _isLoaded = false;
    protected Map<String, String>            _configuration = new HashMap<String, String>();
    protected File                           _file;
    private Map<String, String>              _plainConfiguration;
    private Map<String, Map<String, String>> _hierarchicalConfiguration;


    /**
     * 
     */
    public DefaultConfigurationLoader(String filepath) {

        _file = new File(filepath);
    }


    /**
     * 
     */
    public DefaultConfigurationLoader(File file) {

        _file = file;
    }


    /**
     * Load configuration data from file.
     * @return
     */
    protected abstract void _loadConfiguration();


    protected abstract Map<String, String> _getPlainConfiguration();


    protected abstract Map<String, Map<String, String>> _getHierarchicalConfiguration();


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.configuration.ConfigurationLoader#getConfiguration()
     */
    @Override
    public Map<String, String> getConfiguration() {

        if (! _isLoaded) {
            _loadConfiguration();
            _plainConfiguration = _getPlainConfiguration();
        }

        return _plainConfiguration;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.configuration.ConfigurationLoader#getConfiguration()
     */
    @Override
    public Map<String, String> getPlainConfiguration() {

        if (! _isLoaded) {
            _loadConfiguration();
            _plainConfiguration = _getPlainConfiguration();
        }

        return _plainConfiguration;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.configuration.ConfigurationLoader#getConfiguration()
     */
    @Override
    public Map<String, Map<String, String>> getHierarchicalConfiguration() {

        if (! _isLoaded) {
            _loadConfiguration();
            _hierarchicalConfiguration = _getHierarchicalConfiguration();
        }

        return _hierarchicalConfiguration;
    }
}