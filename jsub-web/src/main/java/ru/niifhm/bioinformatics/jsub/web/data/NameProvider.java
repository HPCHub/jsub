/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;


import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;
import ru.niifhm.bioinformatics.biodb.ReadSet;
import ru.niifhm.bioinformatics.util.StringUtil;


/**
 * @author zeleniy
 */
abstract public class NameProvider {


    protected ReadSet _readSet;
    protected String  _name;
    protected int     _number;


    public static NameProvider factory(Map<String, String> properties) throws Exception {

        if (! properties.containsKey("jsub.name-provider.class")) {
            return new ReadSetNameProvider();
        }

        String property = properties.get("jsub.name-provider.class");
        Class<?> clazz = Class.forName(String.format(
            "ru.niifhm.bioinformatics.jsub.web.data.%sNameProvider", StringUtil.ucfirst(property)
        ));

        Constructor<?> constructor;
        if (! properties.containsKey("jsub.name-provider.prop")) {
            constructor = clazz.getConstructor();
            return (NameProvider) constructor.newInstance();
        }

        property = properties.get("jsub.name-provider.prop");
        String file = properties.get(property);

        constructor = clazz.getConstructor(File.class);
        return (NameProvider) constructor.newInstance(new File(file));
    }


    public NameProvider setNumber(int number) {

        _number = number;
        return this;
    }


    public NameProvider setBaseName(String prefix) {

        _name = prefix;
        return this;
    }


    public NameProvider setReadSet(ReadSet readSet) {

        _readSet = readSet;
        return this;
    }


    abstract public String getName();
}