/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.jsub.Executer;
import ru.niifhm.bioinformatics.jsub.Jsub;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.PipelineLayout;
import ru.niifhm.bioinformatics.jsub.SystemExecuter;


/**
 * Executes user scenario in console mode.
 * @author zeleniy
 */
public class ExecuteCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "execute project scenario";
    boolean                     _isWait     = false;


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.CLICommand#getDescription()
     */
    public String getDescription() {

        return DESCRIPTION;
    }


    public ExecuteCommand() {

        _addDependency(new TestCommand());
        _addDependency(new PrepareCommand());
        _addDependency(new InstallCommand());
        _addDependency(new CopyCommand());
        _addDependency(new AssembleCommand());
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

        Executer executer = new SystemExecuter(project);
//        executer.setWait(_isWait);
        executer.execute();

        Jsub.getInstance().resetLogFilepath();
    }
}