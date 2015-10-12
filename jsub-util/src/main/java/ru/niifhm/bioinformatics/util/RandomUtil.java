/**
 * 
 */
package ru.niifhm.bioinformatics.util;

import java.util.Random;


/**
 * @author zeleniy
 */
public class RandomUtil {


    private RandomUtil() {

    }


    public static String getRandomString() {

        Random random = new Random(System.currentTimeMillis());
        long randomLong = random.nextLong();

        return Long.toHexString(randomLong);
    }


    public static int getInt(int min, int max) {

        java.util.Random random = new java.util.Random();
        return random.nextInt(max - min + 1) + min;
    }
}