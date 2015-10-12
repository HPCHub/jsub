/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.sge;


import java.io.FilenameFilter;
import java.util.regex.Pattern;


/**
 * Grid engine output files filter.
 * @author zeleniy
 */
public class LogFilenameFilter extends GridEngineFilenameFilter implements FilenameFilter {


    public static final Pattern SCRIPTS_LOG_FILES_PATTERN = Pattern.compile("^([A-Za-z0-9_-]+)\\.o(\\d+)$");


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.sge.GridEngineFilenameFilter#getFilenamePattern()
     */
    public Pattern getFilenamePattern() {

        return SCRIPTS_LOG_FILES_PATTERN;
    }
}