/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.jsub.Config;


/**
 * Tests user scenario configuration.
 * @author zeleniy
 */
public class TestCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "test project configuration";


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.CLICommand#getDescription()
     */
    public String getDescription() {

        return DESCRIPTION;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.Command#_execute()
     */
    protected void _execute() throws BuildException {

        if (Config.getInstance().is(Config.FLAG_SKIP_TEST_PHASE)) {
            return;
        }

        try {
            getProject().init().test();
        } catch (Exception e) {
            throw new BuildException(e.getMessage());
        }
    }
}