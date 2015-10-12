/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import ru.niifhm.bioinformatics.jsub.ant.AntProjectBuilder;
import ru.niifhm.bioinformatics.jsub.ant.AntUtil;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.ant.PropertyUtil;
import ru.niifhm.bioinformatics.jsub.job.Job;
import ru.niifhm.bioinformatics.jsub.job.JobException;
import ru.niifhm.bioinformatics.jsub.job.JobFactory;
import ru.niifhm.bioinformatics.jsub.job.ScriptJob;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;


/**
 * User scenario project representation.
 * @author zeleniy
 */
public class Pipeline implements Iterable<Job> {


    public static final String         FINALIZE_COMMAND  = "finalize.py";
    public static final String         DIRECTORY_OUTPUT  = "output";
    public static final String         DIRECTORY_CONFIG  = "config";
    public static final String         DIRECTORY_SCRIPTS = "scripts";
    public static final String         DIRECTORY_LOG     = "log";
    /**
     * Project directory.
     */
    private String                     _directory;
    /**
     * Project type.
     */
    private String                     _type;
    /**
     * Project name.
     */
    private String                     _name;
    /**
     * Project tag.
     */
    private String                     _tag;
    private LinkedHashMap<String, Job> _queue            = new LinkedHashMap<String, Job>();
    private Map<String, Property>      _properties       = new HashMap<String, Property>();
    private List<Property>             _globalProperties;
    private List<Property>             _initialFiles;
    private static Logger              _logger           = Logger.getLogger(Pipeline.class);
    private boolean                    _isInited         = false;
    /**
     * User email address.
     */
    private String                     _email;
    private Skeleton                   _skeleton;


    public static Pipeline newInstance(String name, String tag, String type, String directory) {

        Pipeline pipeline = new Pipeline(name, tag, type, directory);
        Jsub.getInstance().setProject(pipeline);

        return pipeline;
    }


    private Pipeline(String name, String tag, String type, String directory) {

        if (name == null) {
            throw new BuildException("You must specify project name");
        }

        if (type == null) {
            throw new BuildException("You must specify project type");
        }

        if (directory == null) {
            throw new BuildException("You must specify project directory");
        }

        _name = name;
        _tag = tag;
        _type = type;
        _directory = directory;

        _skeleton = new Skeleton(type);
    }


    public Skeleton getSkeleton() {

        return _skeleton;
    }


    public Set<Property> getUserDefinedProperties() {

        Set<Property> properties = new TreeSet<Property>();
        for (Property property : _skeleton.getUserDefinedProperties()) {
            properties.add(property.fromValue(_initialFiles));
        }

        return properties;
    }


    public Set<Property> getUserDefinedFiles() {

        Set<Property> properties = new TreeSet<Property>();
        for (Property property : _skeleton.getUserDefinedFileProperties()) {
            properties.add(property.fromValue(_initialFiles));
        }

        return properties;
    }


    public static String getClearListName(String type) {

        int index = type.indexOf(StringPool.DASH);
        if (index < 0) {
            return StringPool.EMPTY;
        }

        return type.substring(0, index);
    }


    public Iterator<Job> iterator() {

        return _queue.values().iterator();
    }


    public String getEmail() {

        return _email;
    }


    public void setEmail(String email) {

        _email = email;
    }


    public static List<String> getLayoutDirs() {

        return Arrays.asList(DIRECTORY_CONFIG, DIRECTORY_LOG, DIRECTORY_OUTPUT, DIRECTORY_SCRIPTS);
    }


    public PipelineLayout getLayout() {

        String buildDirectory = Config.getInstance().get(Config.PROPERTY_BUILD_DIR);

        if (buildDirectory == null) {
            return new PipelineLayout(_name, _tag, _type, _directory);
        } else {
            return new PipelineLayout(buildDirectory);
        }
    }


    /**
     * Initialize pipeline.
     * Read xml and properties files from config directory.
     * @return
     */
    public Pipeline init() {

        if (! _isInited) {
            _loadProperties();
            _loadQueue();

            _isInited = true;
        }

        return this;
    }


    public Pipeline load() {

        _isInited = false;
        _loadProperties();
        _isInited = true;

        return this;
    }


    public List<Property> getInitialFiles() {

        return _initialFiles;
    }


    public List<Property> getInputFiles() {

        List<Property> result = new ArrayList<Property>();

        for (Property property : _properties.values()) {
            if (PropertyUtil.isInputFileProperty(property)) {
                result.add(property);
            }
        }

        return result;
    }


    public String getType() {

        return _type;
    }


    public String getTag() {

        return _tag;
    }


    public String getDirectory() {

        return _directory;
    }


    public String getName() {

        return _name;
    }


    /**
     * Get scenarion job list.
     * @return
     */
    public List<Job> getJobList() {

        return new ArrayList<Job>(_queue.values());
    }


    public boolean isInitialFile(Property property) {

        for (Property _initialFile : _initialFiles) {
            if (_initialFile.equals(property)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Get scenario last job.
     * @return
     */
    public Job getLastJob() {

        List<Job> list = getJobList();
        return list.get(list.size() - 1);
    }


    private void _setProperty(String name, String value) {

        if (value == null) {
            return;
        }

        Property property = new Property(name, value);
        _properties.put(name, property);
        if (PropertyUtil.isInputFileProperty(name) && ! _isInited) {
            _initialFiles.add(property);
        }
    }


    private void _loadProperties() {

        _initialFiles = new ArrayList<Property>();

        try {
            Set<String> keys = new TreeSet<String>();
            Properties file = new Properties();
            file.load(new FileInputStream(getBuildPropertiesFilepath()));
            Enumeration<Object> enumeration = file.keys();

            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String value = file.getProperty(name);
                keys.add(name);
                _setProperty(name, value);
            }

            /*
             * Запихиваем в _initialFiles все свойства, которые были переданы при помощи опции --properties.
             */
            for (Map.Entry<String, String> entry : Config.getInstance().getMap(Config.PROPERTY_PROPERTIES).entrySet()) {
                _setProperty(entry.getKey(), entry.getValue());
            }

        } catch (Exception e) {
            _logger.error(String.format("Cannot load project build.properties file \"%s\"",
                getBuildPropertiesFilepath()));
        }
    }


    public void setProperty(Property property) {

        Property newProperty;
        String name = property.getName();
        if (name.startsWith("output")) {
            newProperty = new Property("input" + name.substring(name.indexOf(".")), property.getValue());
        } else {
            newProperty = property;
        }

        _properties.put(newProperty.getName(), newProperty);
    }


    /**
     * Get inner ant project.
     * @deprecated hide this from client code.
     * @return
     */
    public Project getAntProject() {

        PipelineLayout layout = getLayout();
        Project project = AntProjectBuilder.getInstance()
            .setProperties(new ArrayList<Property>(_properties.values()))
            .setProperty("project." + Config.PROPERTY_PROJECT_NAME, _name)
            .setProperty("project." + Config.PROPERTY_PROJECT_TYPE, _type)
            .setProperty("project." + Config.PROPERTY_PROJECT_DIR, _directory)
            .setProperty("project.log.dir", layout.getLogDirectory().getPath())
            .setProperty("project.output.dir", layout.getOutputDirectory().getPath())
            .setBuildXmlFile(new File(getBuildXmlFilepath()))
            .create();

        /*
         * Допихиваем в проект все свойства, которые были переданы при помощи опции --properties
         */
        for (Map.Entry<String, String> entry : Config.getInstance().getMap(Config.PROPERTY_PROPERTIES).entrySet()) {
            project.setProperty(entry.getKey(), entry.getValue());
        }

        if (_globalProperties == null) {
            _globalProperties = AntUtil.getGlobalProperties(project);
        } else {
            for (Property property : _globalProperties) {
                project.setProperty(property.getName(), property.getValue());
            }
        }

        return project;
    }


    /**
     * @param name
     * @return
     */
    public Job getJobByName(String name) {

        return _queue.get(name);
    }


    /**
     * Get project build directory.
     * TODO: move this method to {@link PipelineLayout}
     */
    public File getBuildDir() {

        return getLayout().getBuildDirectory();
    }


    /**
     * Get project build.properties file.
     * TODO: move this method to {@link PipelineLayout}
     */
    public String getBuildPropertiesFilepath() {

        return new StringBuilder(getBuildDir().getPath())
            .append(File.separator)
            .append(DIRECTORY_CONFIG)
            .append(File.separator)
            .append("build.properties")
            .toString();
    }


    /**
     * Get project build.xml file.
     * TODO: move this method to {@link PipelineLayout}
     */
    public String getBuildXmlFilepath() {

        return new StringBuilder(getBuildDir().getPath())
            .append(File.separator)
            .append(DIRECTORY_CONFIG)
            .append(File.separator)
            .append("build.xml")
            .toString();
    }


    /**
     * Get project build.nodependencies.xml file.
     * TODO: move this method to {@link PipelineLayout}
     */
    public String getNoDepsBuildXmlFilepath() {

        return new StringBuilder(getBuildDir().getPath())
            .append(File.separator)
            .append(DIRECTORY_CONFIG)
            .append(File.separator)
            .append("build.nodependencies.xml")
            .toString();
    }


    /**
     * Get project output directory.
     * TODO: move this method to {@link PipelineLayout}
     * @return File
     */
    public File getOutputDir() {

        return new File(new StringBuilder(getBuildDir().getPath())
            .append(File.separator)
            .append(DIRECTORY_OUTPUT)
            .append(File.separator)
            .toString());
    }


    /**
     * Get project scripts directory.
     * TODO: move this method to {@link PipelineLayout}
     * TODO: return {@link File}
     * @return String
     */
    public String getScriptsDirectory() {

        return new StringBuilder(getBuildDir().getPath())
            .append(File.separator)
            .append(DIRECTORY_SCRIPTS)
            .append(File.separator)
            .toString();
    }


    /**
     * Get project log directory.
     * @return
     */
    public File getLogDirectory() {

        return new File(new StringBuilder(getBuildDir().getPath())
            .append(File.separator)
            .append(DIRECTORY_LOG)
            .append(File.separator)
            .toString());
    }


    /**
     * Test project configuration.
     */
    public void test() throws Exception {

        List<Property> files = getInitialFiles();
        for (Property file : files) {
            String pathname = file.getValue();

            if (pathname == null) {
                throw new Exception(String.format("You must configure build.properties file [%s]", file.getName()));
            }

            if (pathname.trim().equals(StringPool.EMPTY)) {
                throw new Exception(String.format("You must configure build.properties file [%s]", file.getName()));
            }

            if (! new File(pathname).exists()) {
                throw new Exception(String.format("File \"%s\" not exists in your build.properties", pathname));
            }
        }
    }


    /**
     * Assemble project.
     */
    public void assemble() {

        for (Job job : _queue.values()) {
            try {
                job.parse();
            } catch (JobException e) {
                _logger.warn(String.format(
                    "Cannot assemble task \"%s\" [%s] %s",
                    job.getName(),
                    e.getClass().getName(),
                    e.getMessage()
                ));
            }
        }
    }


    /**
     * Install executables to scripts directory.
     * @throws BuildException
     */
    public void install() throws BuildException {

        try {
            boolean isStarted = false;
            for (Job job : _queue.values()) {

                if (isStarted == false && job.isSkiped() == false) {
                    isStarted = true;
                }

                try {
                    if (isStarted) {
                        job.install();
                    }
                } catch (JobException e) {
                    _logger.error(e.getMessage());
                    throw e;
                }
            }
        } catch (JobException e) {
            throw new BuildException(
                String.format("Cannot install project \"%s\": [%s] %s", _name, e.getClass().getName(), e.getMessage()),
                e);
        }
    }


    private void _loadQueue() {

        _queue = getScenarioJobs(getBuildXmlFilepath());
        for (Job job : _queue.values()) {
            job.setProject(this);
        }
    }


    /**
     * Load targets from build.xml and save they in dependency order.
     * Too bad, too slow...
     * @throws JobException
     */
    public static LinkedHashMap<String, Job> getScenarioJobs(String buildXMLPathname) {

        _logger.debug(String.format("Load user scenario from \"%s\"", buildXMLPathname));

        Project project = AntProjectBuilder.getInstance()
            .setBuildXmlFile(new File(buildXMLPathname))
            .create();

        LinkedHashMap<String, Job> queue = new LinkedHashMap<String, Job>();
        List<Target> targets = AntUtil.getProjectTargets(project);
        List<Target> targetsWithDependencies = new ArrayList<Target>();

        for (Target target : targets) {
            if (AntUtil.hasTargetDependencies(target)) {
                targetsWithDependencies.add(target);
            } else {
                Job job = JobFactory.getInstance(target.getName());
                queue.put(target.getName(), job);
                _logger.trace(String.format("Add \"%s\" to project queue", job.getName()));
            }
        }

        int size = targetsWithDependencies.size();
        for (int i = 0; i < size; i ++) {
            for (Target target : targetsWithDependencies) {
                List<String> dependencies = Collections.list((Enumeration<String>) target.getDependencies());
                boolean isDependencyResolved = false;
                try {
                    for (String dependency : dependencies) {
                        if (! queue.containsKey(dependency)) {
                            throw new Exception();
                        }
                        isDependencyResolved = true;
                    }
                    if (isDependencyResolved && ! queue.containsKey(target.getName())) {
                        Job job = JobFactory.getInstance(target.getName(), dependencies);
                        queue.put(target.getName(), job);
                        _logger.trace(String.format(
                            "Add \"%s\" to project queue with dependencies (%s)",
                            job.getName(),
                            StringUtil.join(dependencies, ",")
                        ));
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        }

        return queue;
    }


    public static String getType(String pipelineName) {

        int index = pipelineName.indexOf(StringPool.DASH);
        if (index > 0) {
            return pipelineName.substring(0, index);
        } else {
            return pipelineName;
        }
    }
}