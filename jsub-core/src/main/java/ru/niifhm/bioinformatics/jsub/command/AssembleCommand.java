/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import org.apache.tools.ant.BuildException;


/**
 * Parse scripts and replace placeholders.
 * Apply plugins handlesr for scripts, if it necessary.
 * @author zeleniy
 */
public class AssembleCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "assemble project befor execute";


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.CLICommand#getDescription()
     */
    public String getDescription() {

        return DESCRIPTION;
    }


    /**
     * 
     */
    public AssembleCommand() {

        _addDependency(new TestCommand());
        _addDependency(new PrepareCommand());
        _addDependency(new InstallCommand());
        _addDependency(new CopyCommand());
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.Command#_execute()
     */
    @Override
    protected void _execute() throws BuildException {

        getProject().init().assemble();
    }
}