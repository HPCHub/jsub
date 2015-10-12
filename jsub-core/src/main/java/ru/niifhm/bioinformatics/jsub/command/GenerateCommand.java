/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import org.apache.tools.ant.BuildException;


/**
 * Generates empty scripts for user scenario, if necessary.
 * @author zeleniy
 */
public class GenerateCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "generate scripts for conveyor layout";


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
    @Override
    protected void _execute() throws BuildException {

    }
}