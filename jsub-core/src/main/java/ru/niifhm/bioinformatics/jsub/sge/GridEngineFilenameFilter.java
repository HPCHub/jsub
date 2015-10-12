/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.sge;


import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Grid engine files filter.
 * @author zeleniy
 */
public class GridEngineFilenameFilter implements FilenameFilter {


    /**
     * File pattern.
     */
    public static final Pattern SCRIPTS_LOG_FILES_PATTERN = Pattern.compile("^([A-Za-z0-9_-]+)\\.o|e\\d+$");


    /*
     * (non-Javadoc)
     * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
     */
    @Override
    public boolean accept(File dir, String name) {

        Matcher matcher = getFilenamePattern().matcher(name);
        return matcher.find();
    }


    /**
     * Get regular expression pattern for looking for fiels.
     * @return
     */
    public Pattern getFilenamePattern() {

        return SCRIPTS_LOG_FILES_PATTERN;
    }
}