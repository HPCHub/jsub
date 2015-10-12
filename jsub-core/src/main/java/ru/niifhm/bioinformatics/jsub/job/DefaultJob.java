package ru.niifhm.bioinformatics.jsub.job;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Layout;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.PropertyMapper;
import ru.niifhm.bioinformatics.jsub.Tools;
import ru.niifhm.bioinformatics.jsub.ant.AntUtil;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.ant.PropertyUtil;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;
import ru.niifhm.bioinformatics.jsub.template.ShellTemplate;
import ru.niifhm.bioinformatics.jsub.template.Template;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.io.FileUtil;


/**
 * Дефолтная реализация интерфейса {@link Job}.
 * Один из самых сложных и запутаных классов jsub-core с, местами,
 * неочевидной логикой. Крайне не рекомендуется вносить изменения
 * без отсутствия тестов, как юнит, так и общих функциональных.
 * Одним из ключевых методов класса является {@link #_executeAntTarget()}.
 * Метод должен выполнятся в любом случае для каждой задачи для
 * обеспечения преемственности входных и выходных параметров.
 * Дочерние классы обеспечивают работу со скриптами написанными на
 * разных языках программирования. Суть разделения по языкам (а именно - bash
 * или НЕ bash) заключается в том, что планировщик OGE на вход принимает только
 * скрипты написанные на bash. Соответственно для скриптов написанных не на bash
 * необходимо сгенерить bash скрипт их которого и будет вызываться сам скрипт
 * и который уже пойдёт на вход планировщику OGE.
 * @author zeleniy
 */
abstract public class DefaultJob implements Job {


    /**
     * Список зависимостей.
     * Содержит в себе значение аттрибута depends элемента target из build.xml.
     */
    protected List<String>        _dependencies              = new ArrayList<String>();
    /**
     * Входные параметры элемента target.
     * Входными элементами являются {@link https://ant.apache.org/manual/Tasks/property.html properties}
     * имена которых начинаются с input.*.
     */
    private List<Property>        _input                     = new ArrayList<Property>();
    /**
     * Выходные параметры элемента target.
     * Выходными элементами являются {@link https://ant.apache.org/manual/Tasks/property.html properties}
     * имена которых начинаются с output.*.
     */
    private List<Property>        _output                    = new ArrayList<Property>();
    /**
     * Job name.
     */
    protected String              _name;
    /**
     * Class logger.
     */
    private static Logger         _logger                    = Logger.getLogger(DefaultJob.class);
    /**
     * Binded project.
     */
    private Pipeline              _project;
    /**
     * Is ant scenario already executed.
     * This variable, probably, for automated tests only and
     * prevent repeats scenario execution.
     */
    private boolean               _isAntScriptExecuted       = false;
    /**
     * Script installer.
     * Install script template from corresponding skeleton dir
     * to project scripts dir.
     */
    protected Installer           _installer;
    /**
     * 
     */
    private List<Property>        _initialProperties         = new ArrayList<Property>();
    /**
     * 
     */
    protected String              _IS_SKIPED                 = "IS_SKIPED";
    /**
     * 
     */
    protected String              _IS_FIRST                  = "IS_FIRST";
    /**
     * 
     */
    protected final static String _PROPERTY_SCRIPT_INSTANCE  = "SCRIPT_INSTANCE";
    /**
     * 
     */
    protected final static String _PROPERTY_INPUT_PROPERTIES = "INPUT_PROPERTIES";
    /**
     * 
     */
    protected final static String _PROPERTY_MAP_ARRAY        = "MAP_ARRAY";
    /**
     * 
     */
    protected final static String _PROPERTY_CLEAR_ARRAY      = "CLEAR_ARRAY";
    /**
     * 
     */
    protected final static String _CLEAR_INPUT_ARRAY         = "CLEAR_INPUT_ARRAY";
    /**
     * 
     */
    protected boolean             _isReplacedToLayout        = false;


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Job#setIndependent()
     */
    public void setIndependent() {

        _dependencies = new ArrayList<String>();
    }


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Job#getTarget()
     */
    public Target getTarget() {

        try {
            return AntUtil.getTargetByName(getProject().getAntProject(), _name);
        } catch (Exception e) {
            _logger.error(String.format("Cannot get ant project for job \"%s\"", _name));
        }

        return null;
    }


    /**
     * Get job specific installer.
     * @return
     */
    public Installer getInstaller() {

        if (_installer == null) {
            int index = _name.indexOf(".");
            if (index >= 0) {
                String extension = _name.substring(index + 1);
                String directory = XConfig.getInstance().getScriptDirectory(extension);
                if (directory == null) {
                    _installer = new ShellInstaller(this);
                } else {
                    _installer = new ScriptInstaller(this);
                }
            } else {
                _installer = new ShellInstaller(this);
            }
        }

        return _installer;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Job#isFirst()
     */
    public boolean isFirst() {

        for (Job job : getProject()) {

            if (job.isSkiped()) {
                continue;
            }

            return getName().equals(job.getName());
        }

        return false;
    }


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Job#getTemplateScriptPathname()
     */
    public String getTemplateScriptPathname() {

        String pathname = null;

        // TODO говнокод. Это должно быть разнесенено по дочерним классам.
        int index = _name.indexOf(".");
        if (index >= 0) {
            String extension = _name.substring(index + 1);
            String directory = XConfig.getInstance().getScriptDirectory(extension);
            if (directory == null) {
                pathname = _getScriptPathname(Config.getInstance().get(Config.SHELL_SCRIPT_DIR));
            } else {
                pathname = _getScriptPathname(directory, extension);
            }
        } else {
            pathname = _getScriptPathname(Config.getInstance().get(Config.SHELL_SCRIPT_DIR));
        }

        return pathname;
    }


    /**
     * Get script pathname.
     * @param dir
     * @return
     */
    private String _getScriptPathname(String dir) {

        StringBuilder builder = new StringBuilder(dir);
        if (! dir.endsWith(File.separator)) {
            builder.append(File.separator);
        }
        builder.append(_name)
            .append(".sh");

        return builder.toString();
    }


    /**
     * Get script pathname.
     * @param dir
     * @return
     */
    private String _getScriptPathname(String dir, String extension) {

        StringBuilder builder = new StringBuilder(dir);
        if (! dir.endsWith(File.separator)) {
            builder.append(File.separator);
        }

        return builder.append(_name).toString();
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.Job#install()
     */
    public String install() throws JobException {

        Installer installer = getInstaller();
        installer.install();

        return installer.getBashScriptPath();
    }


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Job#getShellScriptPathname()
     */
    public String getShellScriptPathname() {

        String pathname;
        int index = _name.indexOf(".");
        if (index >= 0) {
            String extension = _name.substring(index + 1);
            String directory = XConfig.getInstance().getScriptDirectory(extension);
            if (directory == null) {
                pathname = getInstaller().getBashScriptPath();
            } else {
                pathname = FileUtil.getNameWithoutExtension(getInstaller().getBashScriptPath()) + ".sh";
            }
        } else {
            pathname = getInstaller().getBashScriptPath();
        }

        return pathname;
    }


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Job#isSkiped()
     */
    public boolean isSkiped() {

        return Config.getInstance().isSkiped(getName());
    }


    /**
     * @throws JobException
     */
    abstract protected void _parse() throws JobException;


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Job#parse()
     */
    public void parse() throws JobException {

        _logger.debug(String.format("Start parse \"%s\" job", _name));

        try {
            _executeAntTarget();
            _parse();
        } catch (Exception e) {
            throw new JobException(e);
        } finally {
            _logger.debug(String.format("Stop parse \"%s\" job", _name));
        }
    }


    /**
     * @return
     */
    protected Template _getLayout() {

        try {
            String templateName = XConfig.getInstance().getProperty("defaultShellTemplate");
            InputStream stream = Layout.getInstance().getResourceAsInputStream(templateName);

            return new ShellTemplate(FileUtil.read(stream));
        } catch (Exception e) {
            _logger.error(String.format("[%s] Cannot read template file: %s", e.getClass().getName(), e.getMessage()));
        }

        return null;
    }


    /**
     * Execute job specific actions.
     * @throws JobException
     */
    protected void _execute() throws JobException {

        try {
            String extension = FileUtil.getExtension(getInstaller().getBashScriptPath());
            String engine = XConfig.getInstance().getScriptEngine(extension);
            String tool;

            if (engine == null) {
                tool = System.getenv("SHELL");
            } else {
                tool = Tools.getInstance().getToolPath(engine);
                if (tool == null) {
                    tool = engine;
                }
            }

            Runtime runtime = Runtime.getRuntime();
            String command = String.format("%s %s", tool, getInstaller().getBashScriptPath());

            Process process = runtime.exec(command);
            process.waitFor();
            if (Config.getInstance().is(Config.MODE_DEBUG)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    _logger.info(line);
                }
            }
        } catch (Exception e) {
            throw new JobException(String.format("Cannot execute job %s", _name), e);
        }
    }


    /**
     * @param name
     */
    public DefaultJob(String name) {

        _name = name;
    }


    /**
     * @param name
     * @param dependencies
     */
    public DefaultJob(String name, ArrayList<String> dependencies) {

        this(name);
        _dependencies.addAll(dependencies);
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.Job#getProject()
     */
    public Pipeline getProject() {

        return _project;
    }


    /**
     * @param project
     */
    public void setProject(Pipeline project) {

        _project = project;
    }


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.job.Job#getInitialProperties()
     */
    public List<Property> getInitialProperties() {

        return _initialProperties;
    }


    /**
     * Execute ant target associated with this job.
     * @throws Exception
     */
    private void _executeAntTarget() throws Exception {

        if (_isAntScriptExecuted) {
            return;
        }

        _isAntScriptExecuted = true;

        Pipeline pipeline = getProject();
        File outputDir = getProject().getOutputDir();
        Project project = pipeline.getAntProject();
        Target target = AntUtil.getTargetByName(project, _name);

        /*
         * Set target initial properties before project exec.
         * May be it possible to do in constructor?
         */
        _initialProperties = AntUtil.getInputProperties(getTarget());

        target.execute();

        Job[] dependencies = new Job[_dependencies.size()];
        for (int i = 0; i < _dependencies.size(); i ++) {
            dependencies[i] = getProject().getJobByName(_dependencies.get(i));
        }

        List<Property> properties;
        PropertyMapper mapper = new PropertyMapper(outputDir);

        if (hasDependencies()) {
            properties = mapper.getInputProperties(dependencies);
        } else {
            properties = mapper.getInputProperties();
        }

        for (Property property : properties) {
            _logger.trace(String.format("  %s = %s", property.getName(), property.getValue()));
            setInputProperty(property);
        }

        // TODO: move this code to PropertyMapper.
        if (AntUtil.hasOutputProperties(project, target)) {
            List<String> names = AntUtil.getTargetOutputPropertiesNames(project, target);

            for (String name : names) {
                String value = (String) PropertyHelper.getProperty(project, name);

                Property property = new Property(name, value);
                if (PropertyUtil.isFileProperty(property)) {
                    if (! pipeline.isInitialFile(property)) {
                        property = new Property(
                            property.getName(),
                            new File(outputDir, new File(property.getValue()).getName()).getAbsolutePath()
                            );
                    }
                } else {
                    property = new Property(name, value);
                }

                _logger.trace(String.format("  %s = %s", name, value));
                setOutputProperty(property);
            }
        }
    }


    /**
     * @param name
     * @return
     */
    protected Property getInputProperty(String name) {

        for (Property property : _input) {
            if (name.equals(property.getName())) {
                return property;
            }
        }

        return null;
    }


    /**
     * @param name
     * @return
     */
    protected Property getOutputProperty(String name) {

        for (Property property : _output) {
            if (name.equals(property.getName())) {
                return property;
            }
        }

        return null;
    }


    /**
     * @return
     */
    public Map<String, String> getPropertyMap() {

        Map<String, String> map = new HashMap<String, String>();
        for (Task task : getTarget().getTasks()) {
            Property property = Property.factory(task);
            if (! property.isProperty()) {
                continue;
            }

            // i really do not know why they sometimes is null
            Property input = getInputProperty(property.getInput());
            Property output = getOutputProperty(property.getOutput());

            if (input != null && ! input.getValue().equals(StringPool.BLANK) &&
                output != null && ! output.getValue().equals(StringPool.BLANK)) {
                map.put(input.getValue(), output.getValue());
            }
        }

        return map;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.Job#execute()
     */
    public void execute() throws JobException {

        _logger.debug(String.format("Start execute \"%s\" job", _name));

        try {
            _executeAntTarget();
            _execute();
        } catch (Exception e) {
            _logger.debug(String.format("Cannot execute \"%s\" job", _name));
            throw new JobException(e);
        } finally {
            _logger.debug(String.format("Stop execute \"%s\" job", _name));
        }
    }


    /**
     * Get output property.
     * If job still not executed, it is empty.
     * @return
     */
    public List<Property> getOutputProperties() {

        return _output;
    }


    /**
     * Get input properties.
     * @return
     */
    public List<Property> getInputProperties() {

        return _input;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.Job#getName()
     */
    public String getName() {

        return _name;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.Job#getDependenciesNames()
     */
    public List<String> getDependenciesNames() {

        return _dependencies;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.Job#hasDependencies()
     */
    public boolean hasDependencies() {

        return _dependencies.size() != 0;
    }


    /**
     * Set job output property.
     * @param property
     */
    public void setOutputProperty(Property property) {

        _output.add(property);
        getProject().setProperty(property);
    }


    /**
     * Set job input property.
     * @param property
     */
    public void setInputProperty(Property property) {

        _input.add(property);
        getProject().setProperty(property);
    }


    /**
     * Get input properties names.
     * It uses in mapping phase, while input properties not seted.
     * @return
     */
    public List<String> getInputPropertiesNames() {

        try {
            Project project = getProject().getAntProject();
            Target target = AntUtil.getTargetByName(project, _name);

            return AntUtil.getTargetInputPropertiesNames(project, target);
        } catch (Exception e) {
            _logger.error(String.format(
                "Cannot get input properties for \"%s\" job [%s]: %s", getName(), e.getClass().getName(),
                e.getMessage()
                ));
            return new ArrayList<String>();
        }
    }


    /**
     * Get dependencies as job array.
     * @return
     * @throws JobException
     */
    public Job[] getDependencies() throws JobException {

        Job[] dependencies = new Job[_dependencies.size()];

        try {
            if (hasDependencies()) {
                for (int i = 0; i < _dependencies.size(); i ++) {
                    Job dependency = getProject().getJobByName(_dependencies.get(i));
                    dependencies[i] = dependency;
                }
            }

            return dependencies;
        } catch (Exception e) {
            throw new JobException(e);
        }
    }
}