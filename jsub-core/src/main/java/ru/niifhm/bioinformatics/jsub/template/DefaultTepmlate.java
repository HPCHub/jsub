/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.template;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.Executer;
import ru.niifhm.bioinformatics.jsub.Tools;
import ru.niifhm.bioinformatics.jsub.ant.Property;


/**
 * @author zeleniy
 */
public abstract class DefaultTepmlate implements Template {


    private static Logger _logger = Logger.getLogger(DefaultTepmlate.class);
    public final static Pattern   PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([a-z0-9-\\.]+)\\}", Pattern.DOTALL);
    protected Map<String, String> _values             = new HashMap<String, String>();
    protected boolean             _isReplaced         = false;
    protected String              _text;


    public DefaultTepmlate(String text) {

        _text = text;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.template.Template#isEmpty()
     */
    public boolean isEmpty() {

        return getPlaceholders().size() == 0;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.template.Template#getText()
     */
    public String getText() {

        return _text;
    }


    public static boolean isPlaceholder(String string) {

        return PLACEHOLDER_PATTERN.matcher(string).matches();
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.template.Template#getPlaceholders()
     */
    @Override
    public List<String> getPlaceholders() {

        List<String> placeholders = new ArrayList<String>();

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(_text);
        while (matcher.find()) {
            placeholders.add(matcher.group(1));
        }

        return placeholders;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.template.Template#setPlaceholders(java.util.List)
     */
    public Template setPlaceholders(List<Property> values) {

        for (Property property : values) {
            _values.put(property.getName(), property.getValue());
        }

        return this;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.template.Template#setPlaceholders(java.util.Map)
     */
    public void setPlaceholders(Map<String, String> values) {

        _values.putAll(values);
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.template.Template#replace()
     */
    public String replace() {

        if (! _isReplaced) {
            _replaceToolsList();
            _isReplaced = true;
        }

        for (Map.Entry<String, String> property : _values.entrySet()) {

            String propertyName  = property.getKey();
            String propertyValue = property.getValue();

            if (propertyName == null && propertyValue == null) {
                _logger.error("empty property");
                continue;
            } else if (propertyName == null) {
                _logger.error(String.format("property with value %s has empty name", propertyValue));
                continue;
            } else if (propertyValue == null) {
                _logger.error(String.format("property with name %s has empty value", propertyName));
                continue;
            }

            _text = _text.replace("${" + property.getKey() + "}", property.getValue());
        }

        return _text;
    }


    /**
     * Replace tools.xml values in template.
     */
    private void _replaceToolsList() {

        Tools tools = Tools.getInstance();
        Map<String, String> configuration = tools.getConfiguration();
        for (Map.Entry<String, String> property : configuration.entrySet()) {
            String value;
            String name = property.getKey();
            if (name.endsWith("thread")) {
                String tool = tools.getNormalizedName(name);
                value = property.getValue() + " " + Executer.getRequiredCoreNumber(tool);
            } else {
                value = property.getValue();
            }

            _text = _text.replace("${" + name + "}", value);
        }
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.template.Template#replace(java.lang.String, java.lang.String)
     */
    @Override
    public void setPlaceholder(String placeholder, String value) {

        _values.put(placeholder, value);
    }
}