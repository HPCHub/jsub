/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.jsub.Jsub;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.PipelineLayout;
import ru.niifhm.bioinformatics.jsub.QueueExecuter;


/**
 * Executes user scenario through job sheduler.
 * @author zeleniy
 */
public class QueueCommand extends ExecuteCommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "push project to SGE job queue";
    boolean                     _isWait     = false;


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.CLICommand#getDescription()
     */
    public String getDescription() {

        return DESCRIPTION;
    }


    public void setWait(boolean isWait) {

        _isWait = isWait;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.Command#_execute()
     */
    protected void _execute() throws BuildException {

        Pipeline project = getProject().init();
        PipelineLayout layout = project.getLayout();

        Jsub.getInstance().setLogFilepath(layout.getLogFile().getPath());

        QueueExecuter executer = new QueueExecuter(project);
//        executer.setWait(_isWait);
        executer.execute();

        Jsub.getInstance().resetLogFilepath();
    }
}