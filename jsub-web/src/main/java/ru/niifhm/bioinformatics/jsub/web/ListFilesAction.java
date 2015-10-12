/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.jsub.JsubFile;
import ru.niifhm.bioinformatics.jsub.PipelineLayout;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ListFilesAction extends ActionSupport {


    private long _runId;


    public String execute() {

        return Action.SUCCESS;
    }


    public void setRunId(long runId) {

        _runId = runId;
    }


    public List<JsubFile> getFiles() {

        List<JsubFile> jsubFiles = new ArrayList<JsubFile>();

        if (_runId <= 0) {
            return jsubFiles;
        }

        JsubRun run = DAO.findById(JsubRun.class, _runId);
        PipelineLayout layout = new PipelineLayout(
            run.getProjectName(), run.getProjectTag(), run.getProjectType(), run.getProjectDirectory()
        );

        File directory = layout.getOutputDirectory();
        if (! directory.exists()) {
            return jsubFiles;
        }
            
        File[] files = directory.listFiles();
        for (File file : files) {
            jsubFiles.add(new JsubFile(file.getAbsolutePath()));
        }

        return jsubFiles;
    }
}
