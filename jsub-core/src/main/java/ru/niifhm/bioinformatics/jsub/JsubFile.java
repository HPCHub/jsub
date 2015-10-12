/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.File;
import java.net.URI;


/**
 * @author zeleniy
 */
public class JsubFile extends File {


    /**
     * @param pathname
     */
    public JsubFile(String pathname) {

        super(pathname);
        // TODO Auto-generated constructor stub
    }


    /**
     * @param uri
     */
    public JsubFile(URI uri) {

        super(uri);
        // TODO Auto-generated constructor stub
    }


    /**
     * @param parent
     * @param child
     */
    public JsubFile(String parent, String child) {

        super(parent, child);
        // TODO Auto-generated constructor stub
    }


    /**
     * @param parent
     * @param child
     */
    public JsubFile(File parent, String child) {

        super(parent, child);
        // TODO Auto-generated constructor stub
    }


    public long getSize() {

        return length();
    }
}
