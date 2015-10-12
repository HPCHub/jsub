/**
 * 
 */
package ru.niifhm.bioinformatics.util.io;


import java.io.File;
import java.io.IOException;


/**
 * @author zeleniy
 */
public class DirectoryUtil {


    /**
     * 
     */
    private DirectoryUtil() {

    }


    public static File mkdir(File directory) throws Exception {

        if (! directory.mkdir()) {
            throw new Exception(String.format("Cannot create directory \"%s\"", directory.getPath()));
        }

        return directory;
    }


    /**
     * Copy directory.
     * @param from
     * @param to
     * @throws IOException
     */
    public static void copy(File from, File to) throws IOException {

        if (from.isDirectory()) {

            if (! to.exists()) {
                to.mkdir();
            }

            String files[] = from.list();
            for (String file : files) {
                copy(new File(from, file), new File(to, file));
            }

        } else {
            FileUtil.copy(from, to);
        }
    }


    public static String getNormalizedName(String path) {

        if (path.endsWith(File.separator)) {
            return path;
        } else {
            return path + File.separator;
        }
    }
}