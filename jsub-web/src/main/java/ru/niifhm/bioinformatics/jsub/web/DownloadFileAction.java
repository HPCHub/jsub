/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.jsub.PipelineLayout;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class DownloadFileAction extends ActionSupport {


    private String      filename;
    private long        _runId;
    private InputStream fileInputStream;


    public InputStream getFileInputStream() {

        return fileInputStream;
    }


    public void setFilename(String fileName) {

        filename = fileName;
    }


    public String execute() throws Exception {

        JsubRun run = DAO.findById(JsubRun.class, _runId);
        PipelineLayout layout = new PipelineLayout(
            run.getProjectName(), run.getProjectTag(), run.getProjectType(), run.getProjectDirectory()
        );

        File file = new File(layout.getOutputDirectory(), filename);
        System.out.println(file.getAbsolutePath());
        fileInputStream = new FileInputStream(file);

        return SUCCESS;
    }


    public void setRunId(long runId) {

        _runId = runId;
    }
}