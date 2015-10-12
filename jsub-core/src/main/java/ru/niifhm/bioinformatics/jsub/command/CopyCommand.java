/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import java.io.File;
import java.util.List;
import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.util.io.FileUtil;


/**
 * Copies project input files to build dierctory.
 * @author zeleniy
 */
public class CopyCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION     = "copy input files to project directory";


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
    public CopyCommand() {

        _addDependency(new TestCommand());
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.Command#execute()
     */
    @Override
    protected void _execute() throws BuildException {

        if (Config.getInstance().is(Config.FLAG_SKIP_COPY_PHASE)) {
            return;
        }

        // TODO: move to Pipeline#copy()
        try {
            Pipeline pipeline = getProject().init();
            List<Property> files = pipeline.getInputFiles();
            File destination = pipeline.getBuildDir();
            for (Property file : files) {
                File input = new File(file.getValue());
                FileUtil.copy(input, new File(destination, input.getName()));
            }
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}