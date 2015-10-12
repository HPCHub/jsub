/**
 * 
 */
package ru.niifhm.bioinformatics.util;


/**
 * Time utils set.
 * @author zeleniy
 */
public class TimeUtil {


    /**
     * Private constructor.
     */
    private TimeUtil() {

    }


    /**
     * @param value
     * @return
     */
    private static String _getValue(int value) {

        String val = String.valueOf(value);
        if (val.length() == 1) {
            return "0" + val;
        } else {
            return val;
        }
    }


    /**
     * Format seconds to "dd hh:mm:ss".
     * @param num
     * @return
     */
    public static String format(int num) {

        int minutes = num / 60;
        int seconds = num - minutes * 60;
        int hours = minutes / 60;
        minutes = minutes - hours * 60;
        int days = hours / 24;
        hours = hours - days * 24;

        StringBuilder builder = new StringBuilder();
        builder.append(_getValue(seconds));

        if (minutes > 0) {
            builder.insert(0, ":").insert(0, _getValue(minutes));
        } else if (hours > 0 || days > 0) {
            builder.insert(0, "00:");
        } else {
            return builder.toString();
        }

        if (hours > 0) {
            builder.insert(0, ":").insert(0, _getValue(hours));
        } else if (days > 0) {
            builder.insert(0, "00:");
        } else {
            return builder.toString();
        }

        if (days > 0) {
            builder.insert(0, " ").insert(0, days);
        } else {
            return builder.toString();
        }

        return builder.toString();
    }
}