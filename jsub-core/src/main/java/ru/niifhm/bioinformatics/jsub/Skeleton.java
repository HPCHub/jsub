/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.job.Job;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.io.DirectoryUtil;


/**
 * @author zeleniy
 */
public class Skeleton {


    private File   _buildDir;
    private String _name;
    private List<Property> _properties;


    /**
     * 
     */
    public Skeleton(String name) {

        _name = name;
    }


    public Set<Property> getUserDefinedProperties() {

        Set<Property> properties = new TreeSet<Property>();
        for (Property property : getProperties()) {
            if (property.getValue().equals(StringPool.EMPTY)) {
                properties.add(property);
            }
        }

        return properties;
    }


    public Set<Property> getUserDefinedFileProperties() {

        return Properties.getInputFiles(getUserDefinedProperties());
    }


    public List<Property> getProperties() {

        if (_properties == null) {
            _properties = Properties.getProperties(getBuildPropertiesFile());
        }

        return _properties;
    }


    public File getBuildDirectory() {

        if (_buildDir == null) {
            _buildDir = new File(new StringBuilder(Config.getInstance().get(Config.CONVEYOR_DIR))
                .append(File.separator)
                .append(_name)
                .append(File.separator)
            .toString());
        }

        return _buildDir;
    }


    public List<Job> getJobList() {

        String buildXMLPathname = getBuildXmlFile().getPath();
        LinkedHashMap<String, Job> queue = Pipeline.getScenarioJobs(buildXMLPathname);

        return new ArrayList<Job>(queue.values());
    }


    public void install(File destination) throws Exception {

        File directory = null;
        String[] directories = {"scripts", "log", "output", "config"};

        for (String name : directories) {
            directory = new File(destination, name);
            if (! directory.exists()) {
                directory = DirectoryUtil.mkdir(directory);
            }
        }

        DirectoryUtil.copy(getBuildDirectory(), directory);
    }


    public File getBuildXmlFile() {

        return new File(getBuildDirectory(), "build.xml");
    }


    public File getBuildPropertiesFile() {

        return new File(getBuildDirectory(), "build.properties");
    }
}