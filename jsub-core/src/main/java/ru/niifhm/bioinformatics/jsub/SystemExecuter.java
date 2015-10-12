/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.job.Job;
import ru.niifhm.bioinformatics.jsub.job.JobException;


/**
 * Класс реализует исполнение проекта jsub при помощи стандартных средств OS.
 * Используется при вызове:<p>
 * jsub execute ...<p>
 * Данный метод работы jsub на данный момент так и не реализован ввиду неактуальности,
 * свзанной с использованием исключительно {@link QueueExecuter}. Однако сама идея
 * использовать jsub в обычной консоли без использования OGE нравится автору и по сей день... :)
 * Не у всех же есть высокопроизводительный кластер для вычислений...
 * @author zeleniy
 */
public class SystemExecuter extends Executer {


    public SystemExecuter(Pipeline project) {

        super(project);
    }


    public void execute() throws BuildException {

        long runId = 0;

        try {

            runId = registerRun();

            for (Job job : _pipeline.getJobList()) {
                job.setInputProperty(new Property(Property.PROPERTY_PROJECT_RUN_ID, Long.toString(runId)));
                job.parse();
                job.execute();
            }

        } catch (JobException e) {

            if (runId != 0) {
                try {
                    DAO.deleteById(JsubRun.class, runId);
                } catch (Exception daoException) {
                    
                }
            }

            throw new BuildException(String.format(
                "Cannot execute project \"%s\": [%s] %s", _pipeline.getName(),
                e.getClass().getName(),
                e.getMessage()),
            e);
        }
    }
}