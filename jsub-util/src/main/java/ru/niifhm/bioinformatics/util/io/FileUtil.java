package ru.niifhm.bioinformatics.util.io;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.niifhm.bioinformatics.util.StringPool;


public class FileUtil {


    public static Pattern _filenamePattern = Pattern.compile("(.*)\\.\\w+");


    public static String getNameWithoutExtension(File file) {

        return getNameWithoutExtension(file.getName());
    }


    public static String getNameWithoutExtension(String file) {

        Matcher matcher = _filenamePattern.matcher(file);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return StringPool.EMPTY;
        }
    }


    public static String getExtension(File file) {

        return getExtension(file.getName());
    }


    public static String getExtension(String name) {

        String[] parts = name.split("\\.");
        return parts[parts.length - 1];
    }


    public static List<String> getExtensions(File file) {

        return getExtensions(file.getName());
    }


    public static List<String> getExtensions(String name) {

        String[] parts = name.split("\\.");
        String[] result = new String[parts.length - 1];

        System.arraycopy(parts, 1, result, 0, result.length);

        return Arrays.asList(result);
    }


    /**
     * Move file to destination directory.
     * @param file
     * @param directory
     * @throws Exception
     */
    public static void move(File file, File directory) throws Exception {

        File target = new File(directory, file.getName());
        if (! file.renameTo(target)) {
            throw new Exception(String.format("Cannot rename file \"%s\" to \"%s\"", file.getPath(), target.getPath()));
        }
    }


    /**
     * Delete file or directory recursively.
     * @param file
     * @throws IOException
     */
    public static void delete(File file) throws IOException {

        if (! file.isDirectory()) {
            file.delete();
            return;
        }

        // directory is empty, then delete it
        if (file.list().length == 0) {
            file.delete();
            return;
        }

        // list all the directory contents
        File files[] = file.listFiles();
        for (File tmp : files) {
            if (tmp.isDirectory()) {
                delete(tmp);
            } else {
                tmp.delete();
            }
        }

        file.delete();
    }


    /**
     * Read file to string.
     * @param filePathname
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String read(String filePathname) throws FileNotFoundException, IOException {

        return read(new File(filePathname));
    }


    /**
     * Read file to string.
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String read(File file) throws FileNotFoundException, IOException {

        return read(new BufferedReader(new FileReader(file)));
    }


    public static String read(InputStream stream) throws IOException {

        return read(new BufferedReader(new InputStreamReader(stream)));
    }


    public static String read(Reader reader) throws IOException {

        StringBuffer text = new StringBuffer();
        char[] buffer = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buffer)) != - 1) {
            text.append(String.valueOf(buffer, 0, numRead));
        }

        reader.close();

        return text.toString();
    }


    /**
     * Write string to file.
     * If file exists, it will truncated.
     * @param filePathname
     * @param text
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void write(String filePathname, String text) throws FileNotFoundException, IOException {

        write(new File(filePathname), text);
    }


    /**
     * Write string to file.
     * If file exists, it will truncated.
     * @param file
     * @param text
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void write(File file, String text) throws FileNotFoundException, IOException {

        FileOutputStream out = new FileOutputStream(file);
        out.getChannel().truncate(0);
        out.write(text.getBytes());
        out.close();
    }


    /**
     * Copy content of one file to another.
     * If destination file not exists, it will created.
     * @param from
     * @param to
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static int copy(String from, String to) throws FileNotFoundException, IOException {

        return copy(new File(from), new File(to));
    }


    /**
     * Copy content of one file to another.
     * If destination file not exists, it will created.
     * @param from
     * @param to
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static int copy(File from, File to) throws FileNotFoundException, IOException {

        // FileInputStream in = new FileInputStream(from);
        // FileOutputStream out = new FileOutputStream(to);
        //
        // int length = 0;
        // int size = 0;
        // byte[] buffer = new byte[1024];
        // while ((length = in.read(buffer)) > 0) {
        // out.write(buffer, 0, length);
        // size += length;
        // }
        //
        // in.close();
        // out.close();

        return copy(new FileInputStream(from), new FileOutputStream(to));
    }


    public static int copy(InputStream from, OutputStream to) throws FileNotFoundException, IOException {

        int length = 0;
        int size = 0;
        byte[] buffer = new byte[1024];
        while ((length = from.read(buffer)) > 0) {
            to.write(buffer, 0, length);
            size += length;
        }

        from.close();
        to.close();

        return size;
    }


    /**
     * Copy from input stream to file.
     * @deprecated
     * @param in
     * @param to
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static int copy(InputStream from, String to) throws FileNotFoundException, IOException {

        // File f = new File(to);
        // OutputStream out = new FileOutputStream(f);
        //
        // int length = 0;
        // int size = 0;
        // byte buffer[] = new byte[1024];
        // while ((length = in.read(buffer)) > 0) {
        // out.write(buffer, 0, length);
        // size += length;
        // }
        //
        // out.close();
        // in.close();
        //
        // return size;

        return copy(from, new FileOutputStream(new File(to)));
    }
}