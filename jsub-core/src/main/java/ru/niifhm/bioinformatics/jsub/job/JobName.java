/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.job;


import ru.niifhm.bioinformatics.util.StringPool;


/**
 * Job/target name represenatation.
 * @author zeleniy
 */
public class JobName {


    /**
     * Job name.
     */
    String  _name;
    /**
     * Job extension.
     */
    String  _extension;
    /**
     * Has job extension?
     */
    boolean _hasExtension;


    /**
     * Factory method.
     * @param name
     * @return
     */
    public static JobName factory(String name) {

        return new JobName(name);
    }


    /**
     * Constructor.
     * @param name
     */
    public JobName(String name) {

        _name = name;
        int index = _name.lastIndexOf(".");
        if (index >= 0) {
            _extension = _name.substring(index + 1);
            _hasExtension = true;
        } else {
            _extension = StringPool.EMPTY;
            _hasExtension = false;
        }
    }


    /**
     * Get job name extension.
     * @return
     */
    public String getExtension() {

        return _extension;
    }


    /**
     * Has job name extension?
     * @return
     */
    public boolean hasExtension() {

        return _hasExtension;
    }
}