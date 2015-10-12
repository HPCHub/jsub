/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.template;


import java.util.List;
import java.util.Map;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;


/**
 * @author zeleniy
 */
public class TemplateUtil {


    /**
     * Private constructor.
     */
    private TemplateUtil() {

    }


    /**
     * Convert property list to Bash associative array.
     * @param name
     * @param list
     * @return
     */
    public static String getBashAssociativeArrays(String name, List<Property> list) {

        StringBuilder builder = new StringBuilder("declare -A ")
            .append(name)
        .append("=(\n");

        for (Property property : list) {
            builder.append(StringPool.INDENT)
                .append("[")
                .append(property.getName())
                .append("]=\"")
                .append(property.getValue())
                .append(StringPool.QUOTE)
            .append(StringPool.NEW_LINE);
        }

        builder.append(")");
        return builder.toString();
    }


    /**
     * Convert property list to Bash associative array.
     * @param name
     * @param list
     * @return
     */
    public static String getBashAssociativeArrays(String name, Map<String, String> list) {

        StringBuilder builder = new StringBuilder("declare -A ")
            .append(name)
        .append("=(\n");

        for (Map.Entry<String, String> property : list.entrySet()) {
            builder.append(StringPool.INDENT)
                .append("[\"")
                .append(property.getKey())
                .append("\"]=\"")
                .append(property.getValue())
                .append(StringPool.QUOTE)
            .append(StringPool.NEW_LINE);
        }

        builder.append(")");
        return builder.toString();
    }


    /**
     * Get property list as string.
     * @param list
     * @return
     */
    public static String getVariablesList(List<Property> list) {

        int size = list.size();
        String[] strings = new String[size];
        for (int i = 0; i < size; i ++) {
            Property property = list.get(i);
            strings[i] = new StringBuilder()
                .append(property.getName()
                    .replace(StringPool.DOT, StringPool.UNDERLINE)
                    .replace(StringPool.DASH, StringPool.UNDERLINE)
                    .toUpperCase())
                .append(StringPool.EQUAL)
                .append(StringPool.QUOTE)
                .append(property.getValue())
                .append(StringPool.QUOTE)
            .toString();
        }

        return StringUtil.join(strings, StringPool.NEW_LINE);
    }
}