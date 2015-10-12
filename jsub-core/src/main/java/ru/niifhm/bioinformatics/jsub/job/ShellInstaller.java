/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.job;


import java.io.File;
import java.io.IOException;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;
import ru.niifhm.bioinformatics.util.io.FileUtil;


/**
 * Bash scripts installer.
 * @author zeleniy
 */
public class ShellInstaller extends GenericInstaller {


    public ShellInstaller(Job job) {

        super(job);
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Installer#install()
     */
    @Override
    public void install() throws JobException {

        try {
            File template = new File(_job.getTemplateScriptPathname());
            File job = new File(_getBashTemplatePathname());

            FileUtil.copy(template, job);

            job.setExecutable(true);
        } catch (IOException e) {
            throw new JobException(
                String.format("Cannot create job \"%s\" scripts: [%s] %s", _job.getName(), e.getClass().getName(),
                    e.getMessage()), e);
        }

        _bashScriptPath = _getBashTemplatePathname();
        _execScriptPath = _getBashTemplatePathname();
    }


    private String _getBashTemplatePathname() {

        String pathname = null;

        int index = _job.getName().indexOf(".");
        if (index >= 0) {
            String extension = _job.getName().substring(index + 1);
            String directory = XConfig.getInstance().getScriptDirectory(extension);
            if (directory == null) {
                pathname = _getScriptPathname(_job.getProject().getScriptsDirectory());
            } else {
                pathname = _getScriptPathname(_job.getProject().getScriptsDirectory(), extension);
            }
        } else {
            pathname = _getScriptPathname(_job.getProject().getScriptsDirectory());
        }

        return pathname;
    }


    /**
     * Get script pathname.
     * @param dir
     * @return
     */
    private String _getScriptPathname(String dir) {

        StringBuilder builder = new StringBuilder(dir);
        if (! dir.endsWith(File.separator)) {
            builder.append(File.separator);
        }

        return builder.append(_job.getName())
            .append(".sh")
            .toString();
    }


    /**
     * Get script pathname.
     * @param dir
     * @return
     */
    private String _getScriptPathname(String dir, String extension) {

        StringBuilder builder = new StringBuilder(dir);
        if (! dir.endsWith(File.separator)) {
            builder.append(File.separator);
        }

        return builder.append(_job.getName()).toString();
    }
}