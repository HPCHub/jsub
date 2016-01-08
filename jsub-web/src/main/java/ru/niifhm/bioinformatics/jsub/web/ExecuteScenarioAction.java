/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Jsub;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.command.Command;
import ru.niifhm.bioinformatics.jsub.web.data.DataProvider;
import ru.niifhm.bioinformatics.jsub.web.data.NameProvider;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ExecuteScenarioAction extends ActionSupport {


    private static Logger        _logger              = Logger.getLogger(ExecuteScenarioAction.class);
    private static final Pattern _PROPERTY_PATTERN    = Pattern.compile("property\\[(\\d+)\\]");
    private static final Pattern _CLEAR_LIST_PATTERN  = Pattern.compile("clear-list\\[([^\\]]+)\\]");
    private static final Pattern _TARGET_LIST_PATTERN = Pattern.compile("target-list\\[([^\\]]+)\\]");
    private List<String>         _clearList           = new ArrayList<String>();
    private List<String>         _targetList          = new ArrayList<String>();
    private Map<String, String>  _properties;
    private String               _name;
    private String               _type;
    private String               _tag;
    private long                 _runId;
    private boolean              _isForce             = false;
    private String               _startPhase;


    public void setStartPahse(String startPhase) {

        _startPhase = startPhase;
    }


    public boolean getSuccess() {

        return ! hasActionErrors();
    }


    public String getMsg() {

        return hasActionErrors() ? StringUtil.join(getActionErrors(), ";") : Action.SUCCESS;
    }


    public String execute() {

        try {
            DataProvider<Object> provider = DataProvider.factory(_properties)
                .setRunId(_runId)
                .setInputProperties(_properties)
                .setNameProvider(NameProvider.factory(_properties))
                .setRunName(_name)
            .init();

            int count = provider.getCount();
            String description = String.format("Start to execute %s task(s) under \"%s\" name, tag: %s, " +
                            "type: %s, startPhase: %s, targetList: %s, clearList: %s, properties: %s",
                    count, _name, _tag, _type, _startPhase,
                    Arrays.toString(_targetList.toArray()), Arrays.toString(_clearList.toArray()),
                    Arrays.toString(_properties.entrySet().toArray()));
            if (Config.globalLogger != null) {
                Config.globalLogger.info(description);
            } else {
                _logger.info(description);
            }

            String projectName;
            for (Object data : provider) {

                for (Map.Entry<String, String> e : provider.getProperties().entrySet()) {
                    if (e.getKey() != null)
                        _properties.put(e.getKey(), e.getValue());
                    else
                        throw new Exception("Unknown entry: " + e.getValue());
                }

                projectName = count == 1 ? _name : provider.getName();
                _logger.info(String.format("Send \"%s\" project for execution", projectName));

                Config configuration = Config.getInstance();
                for (Map.Entry<String, String> property : _properties.entrySet()) {
                    configuration.set(property.getKey(), property.getValue());
                }

                configuration.set(Config.FLAG_SKIP_COPY_PHASE, true);
                configuration.set(Config.FLAG_SKIP_TEST_PHASE, true);
                configuration.set(Config.PROPERTY_SKIP_LIST, StringUtil.join(_targetList, StringPool.COMMA));
                configuration.set(Config.FLAG_FORCE, _isForce);
                configuration.set(Config.PROPERTY_CLEAR_INPUT, StringUtil.join(_clearList, StringPool.COMMA));
                if (_startPhase != null) {
                    configuration.set(Config.PROPERTY_START_PHASE, _startPhase);
                }

                try {
                    Jsub.getInstance();
                    Pipeline project = Pipeline.newInstance(
                        projectName, _tag, _type, configuration.get(Config.JSUB_WEB_PROJECT_DIR)
                    );

                    if (project.getBuildDir().exists() && ! _isForce) {
                        throw new Exception("Project directory already exists:\n" + project.getBuildDir());
                    } else {
                        Command.factory(Command.COMMAND_CREATE).executeCommand();
                    }

                    java.util.Properties propertiesFile = new java.util.Properties();
                    File file = new File(project.getBuildPropertiesFilepath());
                    propertiesFile.load(new FileInputStream(file));

                    Map<String, String> properties = new HashMap<String, String>(_properties);
                    for (Object key : propertiesFile.keySet()) {
                        String property = (String) key;
                        if (properties.containsKey(property)) {
                            propertiesFile.setProperty(property, properties.get(property));
                            properties.remove(key);
                        }
                    }

                    propertiesFile.putAll(properties);
                    propertiesFile.store(new FileOutputStream(file), null);

                    Command command = Command.factory(Command.COMMAND_QUEUE);
                    command.executeCommand();

                /*
                 * Write some logs...
                 */
                } catch (Exception e) {
                    _setError(String.format(
                        "Cannot execute project \"%s\" [%s]:\n%s",
                        projectName,
                        e.getClass().getName(),
                        e.getClass() == java.lang.NullPointerException.class ?
                                Arrays.toString(e.getStackTrace()) : e.getMessage()
                    ));
                } catch (Throwable t) {
                    _setError(String.format(
                        "Cannot execute project \"%s\" [%s]:\n%s",
                        projectName,
                        t.getClass().getName(),
                        t.getMessage()
                    ));
                }
            }
        } catch (Exception e) {
            _setError(String.format(
                "Cannot execute run \"%s\" [%s]:\n%s\n(%s)",
                _name,
                e.getClass().getName(),
                e.getMessage(),
                Arrays.toString(e.getStackTrace())));
        }

        return Action.SUCCESS;
    }


    private void _setError(String message) {

        setActionErrors(Arrays.asList(message));
        _logger.error(message);
    }


    public void setForce(boolean isForce) {

        _isForce = true;
    }


    public void setType(String type) {

        _type = type;
    }


    public void setName(String name) {

        _name = name;
    }


    public void setTag(String tag) {

        _tag = tag;
    }


    public void setRunId(long runId) {

        _runId = runId;
    }


    public void setProperty(List<String> garbage) {

        if (_properties != null) {
            return;
        }

        _properties = new HashMap<String, String>();

        HttpServletRequest request = ServletActionContext.getRequest();
        Enumeration<String> parameters = request.getParameterNames();

        while (parameters.hasMoreElements()) {
            String name = parameters.nextElement();
            if (name.startsWith("property[")) {
                Matcher matcher = _PROPERTY_PATTERN.matcher(name);
                if (! matcher.matches()) {
                    _logger.error(String.format("Cannot retreive index from property \"%s\"", name));
                    continue;
                }

                int index = Integer.valueOf(matcher.group(1));

                String propertyName = request.getParameter("property-name[" + index + "]");
                String propertyValue = request.getParameter(name);

                _logger.debug(String.format("Get property [%s=%s]", propertyName, propertyValue));

                _properties.put(propertyName, propertyValue);
            } else if (name.startsWith("clear-list[")) {
                Matcher matcher = _CLEAR_LIST_PATTERN.matcher(name);
                if (! matcher.matches()) {
                    _logger.error(String.format("Cannot retreive property name from clear-list \"%s\"", name));
                    continue;
                }

                _clearList.add(matcher.group(1));
            } else if (name.startsWith("target-list[")) {
                Matcher matcher = _TARGET_LIST_PATTERN.matcher(name);
                if (! matcher.matches()) {
                    _logger.error(String.format("Cannot retreive property name from clear-list \"%s\"", name));
                    continue;
                }

                if (request.getParameter(name).equals("off")) {
                    _targetList.add(matcher.group(1));
                }
            }
        }
    }
}