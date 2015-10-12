package ru.niifhm.bioinformatics.util;


import java.util.Enumeration;


public class EnumerationUtil {


    /**
     * Get enumeration size.
     * @param enumeration
     * @return
     */
    public static int size(Enumeration<?> enumeration) {

        int size = 0;
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement();
            size ++;
        }

        return size;
    }
}