package ru.niifhm.bioinformatics.jsub.job;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Properties;
import ru.niifhm.bioinformatics.jsub.ant.AntUtil;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.template.ShellTemplate;
import ru.niifhm.bioinformatics.jsub.template.Template;
import ru.niifhm.bioinformatics.jsub.template.TemplateUtil;
import ru.niifhm.bioinformatics.util.io.FileUtil;


/**
 * Репрезентация {@link Job} для скриптов, написанных на языке отличных от bash. 
 * @author zeleniy
 */
public class ScriptJob extends DefaultJob {


    private static Logger _logger = Logger.getLogger(ScriptJob.class);


    /**
     * @param name
     */
    public ScriptJob(String name) {

        super(name);
    }


    /**
     * @param name
     * @param dependencies
     */
    public ScriptJob(String name, ArrayList<String> dependencies) {

        super(name, dependencies);
    }


    protected void _parse() throws JobException {

        try {
            String templatePathname = getInstaller().getExecScriptPath();
            String text = FileUtil.read(templatePathname);
            Template template = new ShellTemplate(text);

            template.setPlaceholders(getInitialProperties()).replace();

            // TODO: too slow. Do it static.
            List<Property> properties = new ArrayList<Property>();
            Hashtable<String, String> ps = AntUtil.getProjectProperties(getProject().getAntProject());
            for (Map.Entry<String, String> property : ps.entrySet()) {
                properties.add(new Property(property.getKey(), property.getValue()));
            }

            properties.addAll(getOutputProperties());

            template.setPlaceholders(properties);
            text = template.replace();
            FileUtil.write(templatePathname, text);

            if (! _isReplacedToLayout) {
                Template layout = _getLayout();
                if (layout != null) {
                    List<Property> projectInitialValues = getProject().getInitialFiles();
                    List<Property> clearList = new ArrayList<Property>();
                    List<Property> inputs = getInitialProperties();
                    inputs.addAll(getOutputProperties());

                    for (Property property : inputs) {
                        if (! projectInitialValues.contains(property)) {
                            clearList.add(property);
                        }
                    }

                    layout.setPlaceholder(_PROPERTY_INPUT_PROPERTIES, TemplateUtil.getVariablesList(inputs).toString());
                    layout.setPlaceholder(_PROPERTY_MAP_ARRAY,
                        TemplateUtil.getBashAssociativeArrays(_PROPERTY_MAP_ARRAY, getPropertyMap()));
                    layout.setPlaceholder(_IS_SKIPED, String.valueOf(isSkiped()));
                    layout.setPlaceholder(_IS_FIRST, String.valueOf(isFirst()));
                    layout.setPlaceholder(_PROPERTY_CLEAR_ARRAY,
                        TemplateUtil.getBashAssociativeArrays(_PROPERTY_CLEAR_ARRAY, Properties.getInputFiles(clearList)));

                    if (isFirst()) {
                        layout.setPlaceholder(_CLEAR_INPUT_ARRAY, String.valueOf(Boolean.FALSE));
                    } else {
                        layout.setPlaceholder(_CLEAR_INPUT_ARRAY,
                            String.valueOf(Config.getInstance().getList(Config.PROPERTY_CLEAR_INPUT).contains(getName())));
                    }

                    layout.setPlaceholder(_PROPERTY_SCRIPT_INSTANCE, getInstaller().toString());

                    text = layout.replace();
                    _isReplacedToLayout = true;
                    FileUtil.write(getInstaller().getBashScriptPath(), text);
                } else {
                    _logger.error("Cannot get default layout template");
                }
            }
        } catch (Exception e) {
            throw new JobException(e);
        } finally {
            _logger.debug(String.format("Stop parse \"%s\" job", _name));
        }
    }


    public String getTool() {

        _logger.warn("Not implemented");
        return null;
    }
}