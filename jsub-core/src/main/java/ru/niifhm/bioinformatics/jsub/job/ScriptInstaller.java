/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.job;


import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.jsub.Tools;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;
import ru.niifhm.bioinformatics.util.io.FileUtil;


/**
 * Any script language common installer.
 * @author zeleniy
 */
public class ScriptInstaller extends GenericInstaller {


    public ScriptInstaller(Job job) {

        super(job);
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Installer#install()
     */
    @Override
    public void install() throws JobException {

        Installer installer = new ShellInstaller(_job);
        installer.install();

        _execScriptPath = installer.getExecScriptPath();
        _bashScriptPath = FileUtil.getNameWithoutExtension(_execScriptPath) + ".sh";

        try {
            File file = new File(_bashScriptPath);
            if (! file.exists()) {
                file.createNewFile();
            }

            file.setExecutable(true);
            FileUtil.write(file, toString());
        } catch (IOException e) {
            throw new BuildException("Cannot create job file", e);
        }
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {

        String extension = FileUtil.getExtension(_execScriptPath);
        String engine = XConfig.getInstance().getScriptEngine(extension);

        String tool = Tools.getInstance().getToolPath(engine);
        if (tool == null) {
            tool = engine;
        }

        // TODO: temporary environment fix
        return new StringBuilder("export ORACLE_HOME=/u01/app/oracle/11.2/client64\n")
            .append(tool)
            .append(" \"")
            .append(_execScriptPath)
            .append("\"")
            .toString();
    }
}