/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.jsub.Jsub;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.util.StringUtil;


/**
 * @author zeleniy
 */
public abstract class Command {


    public static final String COMMAND_CREATE   = "create";
    public static final String COMMAND_COPY     = "copy";
    public static final String COMMAND_ASSEMBLE = "assemble";
    public static final String COMMAND_EXECUTE  = "execute";
    public static final String COMMAND_QUEUE    = "queue";
    /**
     * Command dependency list.
     */
    protected List<Command>    _dependencies    = new ArrayList<Command>();
    private static Logger      _log             = Logger.getLogger(Command.class);
    private Pipeline            _project;


    /**
     * Factory method.
     * @param commandName
     * @return
     * @throws Exception
     */
    public static Command factory(String commandName) throws Exception {

        try {
            String name = StringUtil.ucfirst(commandName);
            Class<?> clazz = Class.forName("ru.niifhm.bioinformatics.jsub.command." + name + "Command");
            Object command = clazz.newInstance();

            return (Command) command;
        } catch (Exception e) {
            throw new Exception(String.format("Cannot create command \"%s\" [%s] %s", commandName, e.getClass()
                .getName(), e.getMessage()), e);
        }
    }


    public String getDescription() {

        return "";
    }


    /**
     * Execute command specific actions.
     * @throws BuildException
     */
    protected abstract void _execute() throws BuildException;


    public String getName() {

        String name = getClass().getName();
        return name.substring(name.lastIndexOf(".") + 1, name.lastIndexOf("Command")).toLowerCase();
    }


    /**
     * Execute command common actions.
     * @throws BuildException
     */
    public void executeCommand() throws BuildException {

        try {
            _executeDependencies();

            _log.debug(String.format("Start execute \"%s\" command", getName()));
            _execute();
        } catch (BuildException e) {
            throw e;
        } finally {
            _log.debug(String.format("Stop execute \"%s\" command", getName()));
        }
    }


    public Command setProject(Pipeline project) {

        _project = project;
        return this;
    }


    /**
     * Get configured jsub project.
     * @return
     */
    public Pipeline getProject() {

        if (_project == null) {
            return Jsub.getInstance().getProject();
        } else {
            return _project;
        }
    }


    /**
     * Execute dependecy list.
     * @throws Exception
     */
    private void _executeDependencies() throws BuildException {

        for (Command command : _dependencies) {
            command._execute();
        }
    }


    public List<Command> getDependencies() {

        return Collections.unmodifiableList(_dependencies);
    }


    /**
     * Add dependency.
     * @param dependency
     */
    protected void _addDependency(Command dependency) {

        _dependencies.add(dependency);
    }
}