/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import org.apache.tools.ant.BuildException;


/**
 * Fake command. See com.ripcm.bioinformatics.jsub.cli.Main understand how it works.
 * @author zeleniy
 */
public class ServiceCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "execute project through conveyor service";


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
    public ServiceCommand() {

        _addDependency(new TestCommand());
        _addDependency(new PrepareCommand());
        _addDependency(new InstallCommand());
        _addDependency(new CopyCommand());
        _addDependency(new AssembleCommand());
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.Command#_execute()
     */
    @Override
    protected void _execute() throws BuildException {

        getProject().init();
    }
}