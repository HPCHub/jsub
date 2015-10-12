/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.util.Map;
import ru.niifhm.bioinformatics.jsub.Config;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ConfigAction {


    public String execute() {

        return Action.SUCCESS;
    }


    public Map<String, String> getConfig() {

        return Config.getInstance();
    }
}