/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.ant;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import ru.niifhm.bioinformatics.util.EnumerationUtil;


/**
 * Ant utility class.
 * See http://onjava.com/pub/a/onjava/2002/07/24/antauto.html for more information.
 * @author zeleniy
 */
public class AntUtil {


    /**
     * Has target output property set?
     * @param project
     * @param target
     * @return
     */
    public static boolean hasOutputProperties(Project project, Target target) {

        if (getTargetOutputPropertiesNames(project, target) == null) {
            return false;
        } else {
            return true;
        }
    }


    public static List<Property> getGlobalProperties(Project project) {

        List<Property> result = new ArrayList<Property>();
        Hashtable<String, String> properties = getProjectProperties(project);

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (entry.getKey().startsWith("global")) {
                result.add(new Property(entry.getKey(), entry.getValue()));
            }
        }

        return result;
    }


    public static List<Property> getInputProperties(Target target) {

        List<Property> properties = new ArrayList<Property>();
        for (Task task : target.getTasks()) {
            Property property = Property.factory(task);
            if (! property.isProperty()) {
                continue;
            }

            if (! Property.isInputOrStat(property)) {
                continue;
            }

            String value = (String) PropertyHelper.getProperty(target.getProject(), property.getName());
            if (value == null) {
                value = (String) PropertyHelper.getProperty(target.getProject(), Property.getName(property.getValue()));
            }

            properties.add(new Property(property.getName(), value));
        }

        return properties;
    }


    /**
     * Get target input properties names.
     * @param project
     * @param target
     * @return
     */
    public static List<String> getTargetInputPropertiesNames(Project project, Target target) {

        List<String> propertiesNames = new ArrayList<String>();
        Hashtable<String, String> properties = getProjectProperties(project);
        for (Map.Entry<String, String> property : properties.entrySet()) {
            String name = property.getKey();
            if (name.startsWith("input.")) {
                propertiesNames.add(name);
            }
        }

        return propertiesNames;
    }


    /**
     * Get target output propertis names.
     * @param project
     * @param target
     * @return
     */
    public static List<String> getTargetOutputPropertiesNames(Project project, Target target) {

        List<String> propertiesNames = new ArrayList<String>();
        Hashtable<String, String> properties = getProjectProperties(project);
        for (Map.Entry<String, String> property : properties.entrySet()) {
            String name = property.getKey();
            if (name.startsWith("output.")) {
                propertiesNames.add(name);
            }
        }

        return propertiesNames;
    }


    /**
     * Get project properties.
     * @param project
     * @return
     */
    public static Hashtable<String, String> getProjectProperties(Project project) {

        return PropertyHelper.getPropertyHelper(project).getProperties();
    }


    /**
     * Get project properties.
     * @param project
     * @return
     */
    public static String getProjectProperty(Project project, String property) {

        return (String) PropertyHelper.getPropertyHelper(project).getProperties().get(property);
    }


    /**
     * Has target dependencies?
     * @param target
     * @return
     */
    public static boolean hasTargetDependencies(Target target) {

        return EnumerationUtil.size(target.getDependencies()) > 0;
    }


    /**
     * Get project targets.
     * @param project
     * @return
     */
    public static List<Target> getProjectTargets(Project project) {

        List<Target> result = new ArrayList<Target>();
        Hashtable<String, Target> targets = project.getTargets();
        for (Target target : targets.values()) {
            if (! target.getName().equals("") && target.getClass() == org.apache.tools.ant.Target.class) {
                result.add(target);
            }
        }

        return result;
    }


    /**
     * @param project
     * @param targetName
     * @return
     * @throws Exception
     */
    public static Target getTargetByName(Project project, String targetName) throws Exception {

        Hashtable<String, Target> targets = project.getTargets();
        for (Target target : targets.values()) {
            if (target.getName().equals(targetName)) {
                return target;
            }
        }

        throw new Exception(String.format("target \"%s\" not found", targetName));
    }
}