package ru.niifhm.bioinformatics.jsub.ant;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Jsub;


public class AntProjectBuilder {


    public final static String ANT_CONTRIB_PATH = "ant.contrib.path";
    private static Logger      _log             = Logger.getLogger(AntProjectBuilder.class);
    private Project            _project;


    public static AntProjectBuilder getInstance() {

        return new AntProjectBuilder();
    }


    public AntProjectBuilder() {

        _project = new Project();
        _configureProject(_project);
        _setJsubProperties(_project);
    }


    public AntProjectBuilder setBuildXmlFile(File file) {

        _loadBuildFile(_project, file);
        return this;
    }


    public AntProjectBuilder setBuildPropertiesFile(File file) {

        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            Enumeration<Object> enumeration = properties.keys();

            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                String value = properties.getProperty(key);
                _project.setProperty(key, value);
                _log.trace(String.format("Load %s = %s from %s", key, value, file));
            }
        } catch (FileNotFoundException e) {
            _log.error(String.format("Cannot load \"%s\" file: [%s] %s", file.getPath(), e.getClass().getName(),
                e.getMessage()));
        } catch (IOException e) {
            _log.error(String.format("Cannot load \"%s\" file: [%s] %s", file.getPath(), e.getClass().getName(),
                e.getMessage()));
        }

        return this;
    }


    public AntProjectBuilder setProperties(File file) {

        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(file));
            Enumeration<Object> enumeration = properties.keys();

            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                _project.setProperty(key, properties.getProperty(key));
            }
        } catch (Exception e) {
            _log.error(String.format("Cannot load properties file \"%s\"", file.getName()));
        }

        return this;
    }


    public AntProjectBuilder setProperties(List<Property> properties) {

        for (Property property : properties) {
            _project.setProperty(property.getName(), property.getValue());
        }

        return this;
    }


    public AntProjectBuilder setProperty(Property property) {

        _project.setProperty(property.getName(), property.getValue());
        return this;
    }


    public AntProjectBuilder setProperty(String name, String value) {

        _project.setProperty(name, value);
        return this;
    }


    public AntProjectBuilder setBaseDir(File file) {

        _project.setBaseDir(file);
        return this;
    }


    public AntProjectBuilder setBaseDir(String pathname) {

        _project.setBasedir(pathname);
        return this;
    }


    public Project create() {

        _configureLogger(_project);

        return _project;
    }


    /**
     * @return
     */
    public static Project build() {

        return build(Jsub.getInstance().getProject().getNoDepsBuildXmlFilepath());
    }


    /**
     * Create ant project from external build file.
     * @param buildFilePathname
     * @return
     */
    public static Project build(String buildFilePathname) {

        Project project = new Project();
        _configureProject(project);
        _setJsubProperties(project);
        _loadProperties(project);
        _loadBuildFile(project, buildFilePathname);
        _configureLogger(project);

        return project;
    }


    /**
     * Create ant project from jsub build file from resource directory.
     * @param buildFile
     * @return
     */
    public static Project build(File buildFile) {

        Project project = new Project();
        _configureProject(project);
        _setJsubProperties(project);
        _loadProperties(project);
        _loadBuildFile(project, buildFile);
        _configureLogger(project);

        return project;
    }


    /**
     * Load jsub build file from resource directory.
     * @param project
     * @param buildFile
     */
    private static void _loadBuildFile(Project project, File buildFile) {

        ProjectHelper.configureProject(project, buildFile);
        if (project.getName() == null) {
            throw new BuildException("you must specify project name attribute in your build.xml file");
        }
    }


    /**
     * Load exteranl build file.
     * @param project
     * @param buildFilePathname
     */
    private static void _loadBuildFile(Project project, String buildFilePathname) {

        _loadBuildFile(project, new File(buildFilePathname));
    }


    /**
     * @param project
     */
    private static void _configureProject(Project project) {

        project.init();
        project.addTaskDefinition("for", net.sf.antcontrib.logic.ForTask.class);
        project.addTaskDefinition("antfetch", net.sf.antcontrib.logic.ForTask.class);
    }


    private static void _setJsubProperties(Project project) {

        Config configuration = Config.getInstance();
        project.setProperty(Config.ANT_SCRIPT_DIR, configuration.get(Config.ANT_SCRIPT_DIR));
        project.setProperty(Config.CONVEYOR_DIR, configuration.get(Config.CONVEYOR_DIR));
        project.setProperty(ANT_CONTRIB_PATH, configuration.get(Config.LIBRARY_ANT_CONTRIB));

        /*
         * TODO: ебаная затычка, т.к. класс уёбищен и в каждом build-методе вызывается этот.
         * A конфиг может быть не сконфигурён т.к. нахуй не нужен в некоторых ситуациях.
         */
        try {
            project.setProperty(
                Config.PROPERTY_PROJECT_BUILD_DIR,
                Jsub.getInstance().getProject().getBuildDir().getPath()
            );
        } catch (Exception e) {
            
        }
    }


    /**
     * @param project
     */
    private static void _loadProperties(Project project) {

        Config configuration = Config.getInstance();

        try {
            List<Property> properties = ru.niifhm.bioinformatics.jsub.Properties.getProperties(
                new File(Jsub.getInstance().getProject().getBuildPropertiesFilepath())
            );
            for (Property property : properties) {
                project.setProperty(property.getName(), property.getValue());
            }
        } catch (Exception e) {
            _log.error("Cannot load project build.properties file");
        }
    }


    /**
     * @param project
     */
    private static void _configureLogger(Project project) {

        BuildLogger logger = new ru.niifhm.bioinformatics.jsub.ant.Logger();
        logger.setErrorPrintStream(System.err);
        logger.setOutputPrintStream(System.out);
        logger.setMessageOutputLevel(Project.MSG_INFO);

        project.addBuildListener(logger);
    }
}