/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import ru.niifhm.bioinformatics.jsub.Skeleton;
import ru.niifhm.bioinformatics.jsub.ant.AntProjectBuilder;
import ru.niifhm.bioinformatics.jsub.ant.AntUtil;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class UpdateConfigAction extends ActionSupport {


    private String              _type;
    private String[]            _targets;
    private Set<String>         _newProperties     = new HashSet<String>();
    private Properties          _defaultProperties = new Properties();
    private final static Logger _logger            = Logger.getLogger(UpdateConfigAction.class);


    public String[] getNewProperties() {

        return _newProperties.toArray(new String[_newProperties.size()]);
    }


    public String[] getDefaultProperties() {

        return _defaultProperties.keySet().toArray(new String[_defaultProperties.size()]);
    }


    public String execute() {

        try {

            Skeleton skeleton = new Skeleton(_type);

            File propertiesFile = skeleton.getBuildPropertiesFile();
            _defaultProperties.load(new FileInputStream(propertiesFile));

            File buildXml = skeleton.getBuildXmlFile();
            Project antProject = AntProjectBuilder.getInstance()
                .setBuildXmlFile(buildXml)
                .create();

            for (String targetName : _targets) {
                try {
                    Target target = AntUtil.getTargetByName(antProject, targetName);

                    List<Property> targetProperties = AntUtil.getInputProperties(target);
                    for (Property property : targetProperties) {
                        String propertyName = property.getName();
                        if (Property.isInput(property)) {
                            _newProperties.add(propertyName);
                        }
                    }
                } catch (Exception e) {

                }
            }

        } catch (Exception e) {

            String message = String.format("[%s] %s", e.getClass().getName(), e.getMessage());
            setActionErrors(Arrays.asList(message));
            _logger.error(message);

        }

        return Action.SUCCESS;
    }


    public void setTargets(String[] targets) {

        _targets = targets;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), "<br />") : Action.SUCCESS;
    }


    public void setType(String type) {

        _type = type;
    }
}