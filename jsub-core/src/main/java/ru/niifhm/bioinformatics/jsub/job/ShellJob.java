package ru.niifhm.bioinformatics.jsub.job;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 * Репрезентация {@link Job} для скриптов, написанных на bash.
 * @author zeleniy
 */
public class ShellJob extends DefaultJob {


    /**
     * Class logger.
     */
    private static Logger _logger = Logger.getLogger(ShellJob.class);


    /**
     * @param name
     */
    public ShellJob(String name) {

        super(name);
    }


    /**
     * @param name
     * @param dependencies
     */
    public ShellJob(String name, ArrayList<String> dependencies) {

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

                    layout.setPlaceholder(_PROPERTY_SCRIPT_INSTANCE, text);

                    text = layout.replace();
                    _isReplacedToLayout = true;
                } else {
                    _logger.error("Cannot get default layout template");
                }
            }

            FileUtil.write(templatePathname, text);
        } catch (Exception e) {
            throw new JobException(e);
        } finally {
            _logger.debug(String.format("Stop parse \"%s\" job", _name));
        }
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.Job#getTool()
     */
    public String getTool() {

        try {
            String template = FileUtil.read(getTemplateScriptPathname());
            Pattern pattern = Pattern.compile(".*\\$\\{([a-z0-9-]+)\\.path\\}.*", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(template);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            _logger.error(String.format("Cannot retreive tool for job\"%s\"", _name));
        }

        return null;
    }
}