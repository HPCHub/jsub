/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.File;


/**
 * @author zeleniy
 */
public class PipelineLayout {


    private final String               _FILE_LOG          = "jsub.log";
    /**
     * Project build directory.
     */
    private File   _buildDirectory;
    /**
     * Project directory.
     */


    public PipelineLayout(String name, String tag, String type, String directory) {

        _buildDirectory = _getBuildDirectory(name, tag, type, directory);
    }


    public PipelineLayout(String buildDirectory) {

        _buildDirectory = new File(buildDirectory);
    }


    private File _getBuildDirectory(String name, String tag, String type, String directory) {

        StringBuilder builder = new StringBuilder();
        builder.append(directory);
        builder.append(File.separator);
        builder.append(type);
        builder.append(File.separator);
        if (tag != null) {
            builder.append(tag);
            builder.append(File.separator);
        }

        builder.append(name);

        return new File(builder.toString());
    }


    public File getScript(String name) {

        return new File(getScriptDirectory(), name);
    }


    public File getScriptDirectory() {

        return new File(_buildDirectory, Pipeline.DIRECTORY_SCRIPTS);
    }


    public File getLogDirectory() {

        return new File(_buildDirectory, Pipeline.DIRECTORY_LOG);
    }


    public File getLogFile() {

        return new File(getLogDirectory(), _FILE_LOG);
    }


    public File getOutputDirectory() {

        return new File(_buildDirectory, Pipeline.DIRECTORY_OUTPUT);
    }


    public File getBuildDirectory() {

        return _buildDirectory;
    }
}