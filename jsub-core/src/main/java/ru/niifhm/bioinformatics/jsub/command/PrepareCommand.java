package ru.niifhm.bioinformatics.jsub.command;


import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.ant.BuildXml;


/**
 * Creates build.nodependencies.xml file from project original build.xml.
 * @author zeleniy
 */
public class PrepareCommand extends Command {


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.Command#_execute()
     */
    protected void _execute() throws BuildException {

        try {
            Pipeline project = getProject().init();
            BuildXml parser  = BuildXml.factory(project.getBuildXmlFilepath());

            /*
             * Remove dependency attrlibute.
             */
            parser.removeDependencyAttribute()
                .save(project.getNoDepsBuildXmlFilepath());

        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}