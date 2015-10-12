/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.job;


/**
 * Интерфейc для классов-инсталяторов.
 * Под инсталяциейпонимается <a href="http://redmine.ripcm.com/projects/jsub-cli/wiki/Command_list_FAQ#install-установка-скриптов">установка</a>
 * скриптов из их хранилища в директорию проекта. Хранилище
 * скриптов определяется опцией *.script.dir в pom.xml.
 * @author zeleniy
 */
public interface Installer {


    /**
     * Install script to the project build directory.
     * @return installed script pathname
     * @throws JobException
     */
    public void install() throws JobException;


    /**
     * Path to bash script, which will be executed.
     * @return
     */
    public String getBashScriptPath();


    /**
     * Path to script, which will be invoked from bash script.
     * For code, implemented in bash this path will be the same
     * as bash script path. For code implemented in other language
     * it's differ.
     * @return
     */
    public String getExecScriptPath();
}