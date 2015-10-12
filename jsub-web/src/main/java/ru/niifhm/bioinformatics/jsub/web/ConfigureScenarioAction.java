/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.Jsub;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.Skeleton;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;
import ru.niifhm.bioinformatics.jsub.job.Job;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.spb.zeleniy.mproperties.MProperties;
import ru.spb.zeleniy.mproperties.MProperty;
import ru.spb.zeleniy.mproperties.select.EqualCriteria;
import ru.spb.zeleniy.mproperties.select.MAnnotationSelector;
import ru.spb.zeleniy.mproperties.select.MCriteria;
import ru.spb.zeleniy.mproperties.select.MSelector;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ConfigureScenarioAction {


    private String                           _type;
    private List<Property>                   _properties;
    private List<Map<String, String>>        _jobs          = new ArrayList<Map<String, String>>();
    private Map<String, Map<String, String>> _tags          = new HashMap<String, Map<String, String>>();
    private static String                    TAG_ANNOTATION = "tag";
    private final static Logger              _logger        = Logger.getLogger(ConfigureScenarioAction.class);


    public String execute() {

        try {
            Jsub jsub = Jsub.getInstance();

            Skeleton skeleton = new Skeleton(_type);
            for (Job job : skeleton.getJobList()) {
                Map<String, String> JSONJob = new HashMap<String, String>();
                JSONJob.put("name", job.getName());
                _jobs.add(JSONJob);
            }

            _properties = skeleton.getProperties();
            Collections.sort(_properties, new Comparator<Property>() {


                public int compare(Property p1, Property p2) {

                    int result = p1.getName().compareTo(p2.getName());
                    if (p1.getValue().length() == 0 || p1.getValue().contains(StringPool.COMMA)) {
                        return - 1;
                    } else {
                        return result;
                    }
                }
            });

            /*
             * Getting tags sets. See https://trello.com/c/lfuhOKGu/5-hmp-2013
             */
            MProperties properties = new MProperties(skeleton.getBuildPropertiesFile());
            properties.load();
            MSelector selector = new MAnnotationSelector();
            selector.setCriteria(new EqualCriteria(TAG_ANNOTATION, MCriteria.MODE_NAME));
            properties = selector.select(properties);

            Set<String> tags = new HashSet<String>();
            for (MProperty property : properties) {
                tags.add(property.getAnnotation(TAG_ANNOTATION).getValue());
            }

            for (String tag : tags) {
                selector = new MAnnotationSelector();
                selector.setCriteria(new EqualCriteria(tag, MCriteria.MODE_VALUE));
                MProperties select = selector.select(properties);

                Map<String, String> tagSet = new HashMap<String, String>();

                for (MProperty property : select) {
                    tagSet.put(property.getName(), property.getValue());
                }

                _tags.put(tag, tagSet);
            }

        } catch (Exception e) {
            _logger.error(String.format("[%s] %s", e.getClass().getName(), e.getMessage()));
        }

        return Action.SUCCESS;
    }


    public Map<String, Map<String, String>> getTags() {

        return _tags;
    }


    public List<String> getClearList() {

        String type = Pipeline.getType(_type);
        return XConfig.getInstance().getClearList(type);
    }


    public List<Property> getProperties() {

        return _properties;
    }


    public List<Map<String, String>> getJobs() {

        return _jobs;
    }


    public void setType(String type) {

        _type = type;
    }
}