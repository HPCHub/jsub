/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.job;


import java.util.List;
import org.apache.tools.ant.Target;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.ant.Property;


/**
 * Интерфейс для реализации задач. Задача - это программная реализация
 * элемента target файла build.xml сценария. В идеале задача может быть
 * реализована в любом виде - bash-скрипте, скрипте на другом ЯП или в
 * каком либо другом виде. И выполнятся она должна любым из классов типа
 * {@link Executer}.
 * @author zeleniy
 */
public interface Job {


    public Installer getInstaller();


    public boolean isSkiped();


    public void setIndependent();


    public void setProject(Pipeline pipeline);


    public List<Property> getInitialProperties();


    /**
     * Is current job first in the queue.
     * @return
     */
    public boolean isFirst();


    /**
     * Get job corresponding ant target.
     * @return
     */
    public Target getTarget();


    /**
     * Get job template script from corresponding skeleton subdirectory.
     * @return
     */
    public String getTemplateScriptPathname();


    /**
     * @return
     */
    public String getShellScriptPathname();


    /**
     * Get dependencies as string list.
     * @return
     */
    public List<String> getDependenciesNames();


    /**
     * Get job name.
     * @return
     */
    public String getName();


    /**
     * Has job dependencies?
     * @return
     */
    public boolean hasDependencies();


    /**
     * Get job used tool.
     * @return
     */
    public String getTool();


    /**
     * Get job associated project.
     * @return
     */
    public Pipeline getProject();


    /**
     * Set job input property.
     * @param property
     */
    public void setInputProperty(Property property);


    /**
     * Get job input properties.
     * @return
     */
    public List<Property> getInputProperties();


    /**
     * Set job output properties.
     * @param property
     */
    public void setOutputProperty(Property property);


    /**
     * Set job output property.
     * @return
     */
    public List<Property> getOutputProperties();


    /**
     * Create and install job representation script to project build directory.
     * @return
     * @throws JobException
     */
    public String install() throws JobException;


    /**
     * Execute job script.
     * @throws JobException
     */
    public void execute() throws JobException;


    /**
     * Replace placeholders within template to real values.
     * @throws JobException
     */
    public void parse() throws JobException;
}