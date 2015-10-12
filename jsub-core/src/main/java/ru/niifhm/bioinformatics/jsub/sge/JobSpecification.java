package ru.niifhm.bioinformatics.jsub.sge;


import org.hibernate.tool.hbm2x.StringUtils;
import ru.niifhm.bioinformatics.jsub.Executer;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;
import ru.niifhm.bioinformatics.jsub.job.Job;
import ru.niifhm.bioinformatics.jsub.job.JobException;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;


/**
 * SGE specification string builder.
 * The next links will help you, Luke:
 * http://docs.oracle.com/cd/E24901_01/doc.62/e21976/chapter2.htm#autoId87
 * http://arc.liv.ac.uk/SGE/howto/drmaa_java.html
 * http://bokov.net/weblog/programming/sun-grid-engine-how-to-send-job-onto-specific-hosts
 * @author zeleniy
 */
public class JobSpecification {


    private Job           _job;
    private StringBuilder _specification;


    public JobSpecification(Job job) {

        _job = job;
    }


    /**
     * Factory method.
     * @param jobName
     * @return
     */
    public static JobSpecification factory(Job job) {

        return new JobSpecification(job);
    }


    public JobSpecification create() {

        XConfig config = XConfig.getInstance();

        /*
         * Set required processors core number.
         */
        _specification = new StringBuilder("-pe ");
        _specification.append(config.getProperty("gePeName")).append(" ");
        if (_job.isSkiped()) {
            _specification.append(Executer.MINIMUM_CORE_NUMBER);
        } else {
            _specification.append(Executer.getRequiredCoreNumber(_job.getTool()));
        }

        _specification.append(" ");

        String geWcQueue = config.getProperty("geWcQueue");
        if (! StringUtils.isEmpty(geWcQueue)) {
            _specification.append("-q ")
                .append(geWcQueue)
            .append(" ");
        }

        /*
         * Set dependencies.
         */
        if (_job.hasDependencies()) {
            _specification.append(" -hold_jid ");
            _specification.append(StringUtil.join(_job.getDependenciesNames(), StringPool.COMMA));
        }

        /*
         * Set email address.
         */
//        if (_email != null) {
//            _specification.append(" -m e");
//            _specification.append(" -M ");
//            _specification.append(_email);
//        }

        /*
         * Set job name.
         */
        // _specification.append(" -N ");
        // _specification.append(_getJobName());

        /*
         * Set project name.
         */
        // _specification.append(" -P ");
        // _specification.append(_projectName != null ? _projectName : _job.getProject().getName());

        return this;
    }


    public String toString() {

        return _specification.toString();
    }


    public String getJobScriptPathname() throws JobException {

        return _job.install();
    }
}