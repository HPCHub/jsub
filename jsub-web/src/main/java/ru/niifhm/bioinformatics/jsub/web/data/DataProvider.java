/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;


import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import ru.niifhm.bioinformatics.util.StringUtil;


/**
 * @author zeleniy
 */
public abstract class DataProvider<E> implements Iterator<E>, Iterable<E> {


    protected NameProvider        _nameProvider;
    protected Map<String, String> _properties;
    protected long                _runId;
    protected int                 _currentPosition = 0;
    protected String              _runName;


    public static DataProvider factory(Map<String, String> properties) throws Exception {

        if (! properties.containsKey("jsub.data-provider.class")) {
            return new ReadSetDataProvider();
        }

        String providerName = properties.get("jsub.data-provider.class");

        Constructor<?> constructor;
        if (providerName.equals("file") || providerName.equals("map")) {
            Class<?> clazz = Class.forName(String.format(
                "ru.niifhm.bioinformatics.jsub.web.data.%sDataProvider", StringUtil.ucfirst(providerName)
            ));

            if (! properties.containsKey("jsub.data-provider.prop")) {
                throw new Exception("No file specified for data provider");
            }

            String propertyName = properties.get("jsub.data-provider.prop");
            String fileName = properties.get(propertyName);

            constructor = clazz.getConstructor(File.class);
            return (DataProvider) constructor.newInstance(new File(fileName));

        } else {

            Class<?> clazz = Class.forName(String.format(
                "ru.niifhm.bioinformatics.jsub.web.data.%sDataProvider", StringUtil.ucfirst(providerName)
            ));

            constructor = clazz.getConstructor();
            return (DataProvider) constructor.newInstance();
        }
    }


    public DataProvider() {

    }


    public Iterator<E> iterator() {

        return this;
    }


    public DataProvider setRunName(String runName) {

        _runName = runName;
        return this;
    }


    public DataProvider setNameProvider(NameProvider nameProvider) {

        _nameProvider = nameProvider;
        return this;
    }


    public DataProvider setRunId(long runId) {

        _runId = runId;
        return this;
    }


    public DataProvider setInputProperties(Map<String, String> properties) {

        _properties = properties;
        return this;
    }


    abstract public String getName();


    abstract public Map<String, String> getProperties();


    abstract public int getCount();


    abstract public boolean hasNext();


    abstract public E next();


    abstract public E current();


    abstract public void remove();


    abstract public DataProvider<E> init() throws Exception;
}
