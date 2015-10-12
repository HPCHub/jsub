/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;


/**
 * @author zeleniy
 */
public class FileDataProvider<E> extends DataProvider<E> {


    /**
     * 
     */
    private BufferedReader _reader;
    /**
     * 
     */
    private File           _file;
    /**
     * 
     */
    protected String       _current;


    /**
     * 
     */
    public FileDataProvider(File file) {

        _file = file;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#getName()
     */
    @Override
    public String getName() {

        return _nameProvider.getName();
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#getProperties()
     */
    @Override
    public Map<String, String> getProperties() {

        Map<String, String> properties = new HashMap<String, String>();

        /*
         * Выполняем поиск соответствий по локальному конфигу build.properties
         */
        if (_properties.containsKey("jsub.data-provider.value")) {
            String property = _properties.get("jsub.data-provider.value");
            properties.put(property, _current);
        }

        /*
         * Выполняем поиск соответствий по глобальному конфигу xconfig.xml
         */
        List<String> parts = Arrays.asList(_current.split("\\."));
        for (Map.Entry<String, String> map : XConfig.getInstance().getExtensionsToProperties().entrySet()) {
            if (parts.contains(map.getKey())) {
                properties.put(String.format("input.%s", map.getValue()), _current);
                break;
            }
        }

        return properties;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#getCount()
     */
    @Override
    public int getCount() {

        try {
            LineNumberReader reader = new LineNumberReader(new FileReader(_file));

            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {

            }

            count = reader.getLineNumber();
            reader.close();

            return count;
        } catch (Exception e) {

        }

        return 0;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#hasNext()
     */
    @Override
    public boolean hasNext() {

        try {
            _current = _reader.readLine();
        } catch (IOException e) {
            _current = null;
        }

        _currentPosition ++;

        if (_current == null) {
            return false;
        } else {
            return true;
        }
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#next()
     */
    @Override
    public E next() {

        return current();
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#current()
     */
    @Override
    public E current() {

        return (E) _current;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#remove()
     */
    @Override
    public void remove() {

        // TODO Auto-generated method stub
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#_init()
     */
    @Override
    public DataProvider<E> init() throws Exception {

        _reader = new BufferedReader(new FileReader(_file));
        return this;
    }
}
