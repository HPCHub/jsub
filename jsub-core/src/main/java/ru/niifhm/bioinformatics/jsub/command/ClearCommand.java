/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import org.apache.tools.ant.BuildException;


/**
 * Clear project build directory from input files.
 * @author zeleniy
 */
public class ClearCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "clear project build directory";


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

        throw new BuildException("Not implemented");
    }
}