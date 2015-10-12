package ru.niifhm.bioinformatics.util;


public class ArrayUtil {


    /**
     * Remove first element from array.
     * @param array
     * @return
     */
    public static String[] shift(String[] array) {

        String[] result = new String[array.length - 1];
        for (int i = 1; i < array.length; i ++) {
            result[i - 1] = array[i];
        }

        return result;
    }
}