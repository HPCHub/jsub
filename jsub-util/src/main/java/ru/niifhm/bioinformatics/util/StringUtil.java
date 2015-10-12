package ru.niifhm.bioinformatics.util;


import java.util.Arrays;
import java.util.Collection;


public class StringUtil {


    /**
     * Lead to upper case first string letter.
     * @param string
     * @return
     */
    public static String ucfirst(String string) {

        if (string == null || string.length() == 0) {
            return string;
        } else if (string.length() == 1) {
            return string.toUpperCase();
        } else {
            return string.substring(0, 1).toUpperCase() + string.substring(1);
        }
    }


    /**
     * Join sting list to single string by separator.
     * @param list
     * @param conjunction
     * @return
     */
    public static String join(Collection<String> list, String conjunction) {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first) {
                first = false;
            } else {
                sb.append(conjunction);
            }

            sb.append(item);
        }

        return sb.toString();
    }


    public static String join(String[] list, String conjunction) {

        return join(Arrays.asList(list), conjunction);
    }
}