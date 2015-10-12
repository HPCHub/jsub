/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import org.apache.tools.ant.BuildException;


/**
 * Create graph representation of user scenario.
 * Wafted by this artyicle http://chihungchan.blogspot.com/2007/07/sge-grid-job-dependency.html
 * Needs installed "dot" programm from "graphviz" package.
 * @author zeleniy
 */
public class GraphCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "create project scenario graph representation";


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