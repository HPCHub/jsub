/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.ant.PropertyUtil;
import ru.niifhm.bioinformatics.jsub.job.Job;


/**
 * Property mapper.
 * Map previous job output properties to the input properies of current job.
 * @author zeleniy
 */
public class PropertyMapper {


    private File _outputDir;


    public PropertyMapper(File outputDir) {

        _outputDir = outputDir;
    }


    /**
     * TODO: похоже на бред, да ? Запихать в project всё то, что только
     * что из него считали ?
     * Get input properties for independent job.
     * @return
     */
    public List<Property> getInputProperties() {

        List<Property> properties = new ArrayList<Property>();

        List<Property> projectProperties = Jsub.getInstance().getProject().getInputFiles();
        for (Property property : projectProperties) {
            properties.add(new Property(property.getName(), property.getValue()));
        }

        return properties;
    }


    /**
     * Get input properties for dependent job.
     * @param dependencies
     * @return
     */
    public List<Property> getInputProperties(Job[] dependencies) {

        List<Property> dependenciesProperties = _getDependenciesInputProperties(dependencies);
        List<Property> properties = new ArrayList<Property>();

        List<String> index = new ArrayList<String>();
        for (Property property : dependenciesProperties) {
            String outputPropertyName = property.getName();
            String outputPropertyPostfix = outputPropertyName.substring(outputPropertyName.indexOf("."));
            String inputPropertyName = "input" + outputPropertyPostfix;

            if (PropertyUtil.isFileProperty(inputPropertyName)) {
                property = new Property(
                    inputPropertyName, new File(_outputDir, new File(property.getValue()).getName()).getAbsolutePath()
                );
            } else {
                property = new Property(inputPropertyName, property.getValue());
            }

            properties.add(property);
            index.add(inputPropertyName);
        }

        List<Property> inputProperties = getInputProperties();
        for (Property property : inputProperties) {
            if (index.contains(property.getName())) {
                continue;
            } else {
                properties.add(property);
            }
        }

        return properties;
    }


    /**
     * Get input properties from dependencies.
     * @param dependencies
     * @return
     */
    private List<Property> _getDependenciesInputProperties(Job[] dependencies) {

        List<Property> properties = new ArrayList<Property>(dependencies.length);
        for (int i = 0; i < dependencies.length; i ++) {
            Job dependency = dependencies[i];
            List<Property> output = dependency.getOutputProperties();
            for (Property property : output) {
                if (property == null) {
                    continue;
                } else {
                    properties.add(property);
                }
            }
        }

        return properties;
    }
}