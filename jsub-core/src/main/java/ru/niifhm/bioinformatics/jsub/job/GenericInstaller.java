/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.job;


/**
 * Базовый инсталятор скриптов.
 * @author zeleniy
 */
public abstract class GenericInstaller implements Installer {


    protected Job    _job;
    protected String _bashScriptPath;
    protected String _execScriptPath;


    public GenericInstaller(Job job) {

        _job = job;
    }


    public String getBashScriptPath() {

        return _bashScriptPath;
    }


    public String getExecScriptPath() {

        return _execScriptPath;
    }
}