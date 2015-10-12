/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Jsub;
import com.opensymphony.xwork2.Action;


/**
 * List scenarios action.
 * Generate and sort list of available scenarios.
 * @author zeleniy
 */
public class ScenarioListAction {


    /**
     * Available scenarios.
     */
    private List<Map<String, String>> _scenarios = new ArrayList<Map<String, String>>();
    private final static Logger _logger = Logger.getLogger(ScenarioListAction.class);


    /**
     * Generate and sort list of available scenarios.
     * @return
     */
    public String execute() {

        try {
            Jsub jsub = Jsub.getInstance();
            Config configuration = Config.getInstance();

            File skeletonDir = new File(configuration.get(Config.CONVEYOR_DIR));
            //_logger.error(String.format("my [%s] directory", skeletonDir));
            //System.out.println(String.format("my [%s] directory", skeletonDir));
            File[] files = skeletonDir.listFiles();
            if (files == null) {
                throw new Exception(String.format("Cannot list files in [%s] directory", skeletonDir));
            }

            for (File file : files) {
                if (! file.isDirectory()) {
                    continue;
                }

                if (file.isHidden()) {
                    continue;
                }

                Map<String, String> scenario = new HashMap<String, String>();
                scenario.put("name", file.getName());

                _scenarios.add(scenario);
            }

            Collections.sort(_scenarios, new Comparator<Map<String, String>>() {
                public int compare(Map<String, String> m1, Map<String, String> m2) {
                    return m1.get("name").compareTo(m2.get("name"));
                }
            });
        } catch (Exception e) {
            _logger.error(String.format("[%s] %s", e.getClass().getName(), e.getMessage()));
        } 

        return Action.SUCCESS;
    }


    /**
     * Gett scenarios list.
     * @return
     */
    public List<Map<String, String>> getScenarios() {

        return _scenarios;
    }
}