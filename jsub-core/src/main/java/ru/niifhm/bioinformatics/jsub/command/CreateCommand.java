/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import java.io.File;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.Skeleton;


/**
 * Creates new project directory layout.
 * @author zeleniy
 */
public class CreateCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "creates project directory layout";
    private static final Logger _logger     = Logger.getLogger(CreateCommand.class);


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

        Pipeline pipeline = getProject();
        File buildDir = pipeline.getBuildDir();

        try {
            if (Config.getInstance().is(Config.FLAG_FORCE)) {
                if (! buildDir.exists() && ! buildDir.mkdirs()) {
                    throw new BuildException(String.format("Cannot create build directory \"%s\"", buildDir.getPath()));
                }
            } else {
                if (buildDir.exists()) {
                    throw new BuildException(String.format("Build directory \"%s\" already exists", buildDir.getPath()));
                } else if (! buildDir.mkdirs()) {
                    throw new BuildException(String.format("Cannot create build directory \"%s\"", buildDir.getPath()));
                }
            }

            Skeleton skeleton = new Skeleton(pipeline.getType());
            skeleton.install(buildDir);

            _logger.debug(String.format("Create new project in \"%s\"", buildDir.getAbsolutePath()));

        } catch (Exception e) {
            String message = String.format(
                "Cannot execute \"create\" command [%s] %s",
                e.getClass().getName(),
                e.getMessage()
            );

            _logger.error(message);
            throw new BuildException(message, e);
        }
    }
}