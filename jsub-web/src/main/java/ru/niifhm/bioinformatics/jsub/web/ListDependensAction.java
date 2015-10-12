/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.Skeleton;
import ru.niifhm.bioinformatics.jsub.ant.BuildXml;
import ru.niifhm.bioinformatics.util.StringPool;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ListDependensAction extends ActionSupport {


    private String              _projectType;
    private String              _property;
    private List<String>        _targets;
    private static final Logger _LOGGER = Logger.getLogger(ListDependensAction.class);


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtils.join(getActionErrors(), StringPool.NEW_LINE) : Action.SUCCESS;
    }


    public String execute() {

        try {

            Skeleton skeleton = new Skeleton(_projectType);
            BuildXml buildXml = new BuildXml(skeleton.getBuildXmlFile().getPath());
            _targets = buildXml.getDependentTargets(_property);

        } catch (Exception e) {
            String errorMessage = String.format("Cannot list dependences for property \"%s\" [%s] %s", _property,
                e.getClass().getName(), e.getMessage());
            _LOGGER.error(errorMessage);
            setActionErrors(Arrays.asList(errorMessage));
        }

        return Action.SUCCESS;
    }


    public List<String> getTargets() {

        return _targets;
    }


    public void setProjectType(String projectType) {

        _projectType = projectType;
    }


    public void setProperty(String property) {

        _property = property;
    }
}