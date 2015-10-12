/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.ant;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.Task;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;
import ru.niifhm.bioinformatics.jsub.template.DefaultTepmlate;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.io.FileUtil;


/**
 * @author zeleniy
 */
public class Property extends ru.niifhm.bioinformatics.util.Property {


    public static final String        PROPERTY_DESTINATION     = "destination";
    public static final String        PROPERTY_MOVE_TO_BASEDIR = "move.to.basedir";
    public static final String        PROPERTY_PROJECT_RUN_ID  = "project.run.id";
    public static final String        PROPERTY_FIXED_DIR       = "fixed.dir";
    private static final String       _INPUT                   = String.format("%s%s", "input", StringPool.DOT);
    private static final String       _STAT                    = String.format("%s%s", "stat", StringPool.DOT);
    private static final String       _OUTPUT                  = String.format("%s%s", "output", StringPool.DOT);
    private static final String       _FILE                    = String.format("%s%s", StringPool.DOT, "file");
    private static final String       _PROPERTY                = "property";
    private static final String       _PROPERTY_REGEX          = "propertyregex";
    Task                              _task;
    private static final List<String> _PROPERTIES              = Arrays.asList(
        _PROPERTY, _PROPERTY_REGEX
    );


    /**
     * Factory method.
     * @param task
     * @return
     */
    public static Property factory(Task task) {

        if (task.getTaskName().equals(_PROPERTY_REGEX)) {
            return new PropertyRegex(task);
        } else {
            return new Property(task);
        }
    }


    public Property(Task task) {

        super(
            (String) task.getRuntimeConfigurableWrapper().getAttributeMap().get("name"),
            (String) task.getRuntimeConfigurableWrapper().getAttributeMap().get("value"));

        _task = task;
    }


    public Property(String name, String value, Task task) {

        super(name, value);
        _task = task;
    }


    public Property(String name, String value) {

        super(name, value);
    }


    public Property fromValue(Property property) {

        return new Property(getName(), property.getValue());
    }


    public Property fromValue(List<Property> properties) {

        for (Property property : properties) {
            if (getName().equals(property.getName())) {
                return new Property(getName(), property.getValue());
            }
        }

        return this;
    }


    public String getInput() {

        String input = (String) _task.getRuntimeConfigurableWrapper().getAttributeMap().get("name");
        if (DefaultTepmlate.isPlaceholder(input)) {
            return getName(input);
        } else {
            return input;
        }
    }


    public String getOutput() {

        String output = (String) _task.getRuntimeConfigurableWrapper().getAttributeMap().get("value");
        if (DefaultTepmlate.isPlaceholder(output)) {
            return getName(output);
        } else {
            return output;
        }
    }


    public Property convert() {

        if (isInput(this)) {
            return new Property(String.format("%s%s", _OUTPUT, getClearName()), getValue());
        } else if (isOutput(this)) {
            return new Property(String.format("%s%s", _INPUT, getClearName()), getValue());
        } else {
            return this;
        }
    }


    public static String convert(String property) {

        if (isInput(property)) {
            return String.format("%s%s", _OUTPUT, getClearName(property));
        } else if (isOutput(property)) {
            return String.format("%s%s", _INPUT, getClearName(property));
        } else {
            return property;
        }
    }


    /**
     * Get property name from its placeholder.
     * Convert ${input.fastq.file} to input.fastq.file, for example.
     * @param placeholder
     * @return
     */
    public static String getName(String placeholder) {

        if (placeholder == null) {
            return null;
        }

        String tmp = placeholder.substring(2);
        return tmp.substring(0, tmp.length() - 1);
    }


    /**
     * Get property name without prefix.
     * Return fastq.file for input.fastq.file, for example.
     * @return
     */
    public String getClearName() {

        return getClearName(getName());
    }


    public static String getClearName(String name) {

        return name.substring(name.indexOf(StringPool.DOT) + 1);
    }


    /**
     * Is task represent some kind of property.
     * @return
     */
    public boolean isProperty() {

        if (_task != null) {
            return _PROPERTIES.contains(_task.getTaskName());
        } else {
            return true;
        }
    }


    public static boolean isOutput(Property property) {

        return isOutput(property.getName());
    }


    public static boolean isOutput(String property) {

        return property.startsWith(_OUTPUT);
    }


    public static boolean isInputOrStat(Property property) {

        return isInputOrStat(property.getName());
    }


    public static boolean isInputOrStat(Map.Entry<String, String> property) {

        return isInputOrStat(property.getKey());
    }


    public static boolean isInputOrStat(String property) {

        return property.startsWith(_INPUT) || property.startsWith(_STAT);
    }


    public static boolean isInput(Property property) {

        return isInput(property.getName());
    }


    public static boolean isInput(Map.Entry<String, String> property) {

        return isInput(property.getKey());
    }


    public static boolean isInput(String property) {

        return property.startsWith(_INPUT);
    }


    public static String getInputPropertyNameByFilename(String filename) {

        String property = getPropertyNameByFilename(filename);
        if (property.equals(StringPool.EMPTY)) {
            return property;
        }

        return new StringBuilder(_INPUT)
            .append(property)
            .toString();
    }


    public static String getOutputPropertyNameByFilename(String filename) {

        String property = getPropertyNameByFilename(filename);
        if (property.equals(StringPool.EMPTY)) {
            return property;
        }

        return new StringBuilder(_OUTPUT)
            .append(StringPool.DOT)
            .append(property)
            .toString();
    }


    public static String getPropertyNameByFilename(String filename) {

        String extension = FileUtil.getExtension(filename);
        String name = XConfig.getInstance().getExtensionsToProperties().get(extension);

        if (name == null) {
            name = StringPool.EMPTY;
        }

        return name;
    }
}