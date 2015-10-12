/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.ant;


/**
 * @author zeleniy
 */
public class PropertyUtil {


    private PropertyUtil() {

    }


    public static boolean isFileProperty(ru.niifhm.bioinformatics.util.Property property) {

        return isFileProperty(property.getName());
    }


    public static boolean isFileProperty(String property) {

        return property.endsWith(".file");
    }


    public static boolean isInputFileProperty(ru.niifhm.bioinformatics.util.Property property) {

        return isInputFileProperty(property.getName());
    }


    public static boolean isInputFileProperty(String name) {

        if (name.startsWith("input.") && isFileProperty(name)) {
            return true;
        } else {
            return false;
        }
    }
}