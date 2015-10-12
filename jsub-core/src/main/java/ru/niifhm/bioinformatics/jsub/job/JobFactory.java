/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.job;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;
import ru.niifhm.bioinformatics.util.StringUtil;


/**
 * Job factory.
 * Generates Job class instances corresponding Jsub configuration,
 * if job type not set explict.
 * @author zeleniy
 */
public class JobFactory {


    /**
     * Class logger.
     */
    private static Logger _log = Logger.getLogger(JobFactory.class);


    /**
     * @param name
     * @param type
     * @return
     */
    public static DefaultJob getInstance(String name) {

        String type = _getJobTypeByName(name);
        DefaultJob job = null;

        try {
            Class<?> clazz;

            try {
                clazz = Class.forName("ru.niifhm.bioinformatics.jsub.job." + StringUtil.ucfirst(type) + "Job");
            } catch (ClassNotFoundException e) {
                clazz = Class.forName("ru.niifhm.bioinformatics.jsub.job.ScriptJob");
            }

            Constructor<?> constructor = clazz.getConstructor(String.class);
            job = (DefaultJob) constructor.newInstance(name);
        } catch (Exception e) {
            _log.error(String.format("Cannot create job for type %s: %s", StringUtil.ucfirst(type), e.getMessage()));
        }

        return job;
    }


    /**
     * @param name
     * @param dependencies
     * @param type
     * @return
     */
    public static DefaultJob getInstance(String name, List<String> dependencies) {

        String type = _getJobTypeByName(name);
        DefaultJob job = null;

        try {
            Class<?> clazz;

            try {
                clazz = Class.forName("ru.niifhm.bioinformatics.jsub.job." + StringUtil.ucfirst(type) + "Job");
            } catch (ClassNotFoundException e) {
                clazz = Class.forName("ru.niifhm.bioinformatics.jsub.job.ScriptJob");
            }

            Constructor<?> constructor = clazz.getConstructor(String.class, ArrayList.class);
            job = (DefaultJob) constructor.newInstance(name, dependencies);
        } catch (Exception e) {
            _log.error(String.format(
                "[%s] Cannot create job for type \"%s\": %s", e.getClass().getName(), type, e.getMessage()
            ));
        }

        return job;
    }


    private static String _getJobTypeByName(String name) {

        XConfig config  = XConfig.getInstance();
        JobName jobName = JobName.factory(name);

        if (jobName.hasExtension()) {
            return config.getScriptEngine(jobName.getExtension());
        } else {
            return config.getProperty("defaultJobType");
        }
    }
}