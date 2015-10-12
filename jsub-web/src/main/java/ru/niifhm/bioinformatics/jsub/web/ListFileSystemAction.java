/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.io.File;
import java.util.Arrays;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ListFileSystemAction extends ActionSupport {


    private File[]              _files;
    private String              _directory = File.separator;
    private static final Logger LOGGER     = Logger.getLogger(ListFileSystemAction.class);


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), StringPool.NEW_LINE) : Action.SUCCESS;
    }


    public void setDirectory(String directory) {

        if (! (directory == null || directory.equals(StringPool.EMPTY))) {
            _directory = directory;
        }
    }


    /*
     * (non-Javadoc)
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    public String execute() {

        try {

            File directory = new File(_directory);
            _files = directory.listFiles();
            Arrays.sort(_files);

        } catch (Exception e) {
            String errorMessage = String.format(
                "Cannot list files for directory \"%s\" [%s] %s",
                _directory,
                e.getClass().getName(),
                e.getMessage());
            LOGGER.debug(errorMessage);
            setActionErrors(Arrays.asList(errorMessage));
        }

        return Action.SUCCESS;
    }


    public File[] getFiles() {

        return _files;
    }
}