/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.job.Job;
import ru.niifhm.bioinformatics.jsub.sge.JobSpecification;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.StringUtil;


/**
 * Класс реализует исполнение проекта jsub при помощи планировщика OGE.
 * Доступ к планировщику реализован при помощи 
 * {@link http://redmine.ripcm.com/projects/jsub/wiki/All_about_DRMAA_API DRMAA API}.
 * @author zeleniy
 */
public class QueueExecuter extends Executer {


    private static Logger _logger = Logger.getLogger(QueueExecuter.class);


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.Executer#Executer
     */
    public QueueExecuter(Pipeline project) {

        super(project);
    }


    /**
     * Исполнение проекта.
     * Метод при помощи DRMAA API соединяется с планировщиком OGE и передаёт
     * ему на вход последовательность задач, соответствующих скриптам в директории
     * scripts/ проекта.
     */
    public synchronized void execute() throws BuildException {

        Session session = null;
        long runId  = 0;

        try {

            /*
             * Соединяемся с планировщиком.
             */
            SessionFactory factory = SessionFactory.getFactory();
            session = factory.getSession();

            /*
             * Регистрируем run в БД.
             */
            runId = registerRun();

            /*
             * Определяем первую задачу проекта с которой следует выполнять проект.
             * Затем в цикле последовательно проходимся по каждой задачи пайплайна
             * и отправляем соответствующие скрипты на исполнение в очередь планировщика.
             */
            String startPhase = Config.getInstance().get(Config.PROPERTY_START_PHASE);
            boolean isStarted = false;
            for (Job job : _pipeline) {

                /*
                 * Пропускаем первые задачи, до startPhase.
                 */
                if (! isStarted && job.getName().equals(startPhase)) {
                    isStarted = true;
                }

                if (! isStarted) {
                    continue;
                }

                /*
                 * Конфигурируем JobTemplate и отправляем его в очередь.
                 * JobTemplate имеет достаточно невнятное API - часть опций
                 * конфигурится при помощи отдельных сеттеров, часть при помощи
                 * обычной CLI-строки. Для формирование CLI-строки используется
                 * класс JobSpecification.
                 */
                session.init(StringPool.EMPTY);
                JobTemplate jt = session.createJobTemplate();

                job.setInputProperty(new Property(Property.PROPERTY_PROJECT_RUN_ID, Long.toString(runId)));
                job.parse();

                jt.setEmail(new String[] {_pipeline.getEmail()});
                jt.setJobName(job.getName());
                jt.setWorkingDirectory(_pipeline.getBuildDir().getPath());
                jt.setOutputPath(StringPool.COLON + _pipeline.getLogDirectory().getPath());
                jt.setErrorPath(StringPool.COLON + _pipeline.getLogDirectory().getPath());
                jt.setNativeSpecification(JobSpecification.factory(job).create().toString());
                jt.setRemoteCommand(job.getShellScriptPathname());

                String jobId = session.runJob(jt);
                session.deleteJobTemplate(jt);
                session.exit();

                /*
                 * Пишем логи.
                 */
                if (job.hasDependencies()) {
                    _logger.info(String.format(
                        "Job \"%s\" has been submitted with id %s after [%s]",
                        job.getName(),
                        jobId,
                        StringUtil.join(job.getDependenciesNames(), StringPool.COMMA)
                    ));
                } else {
                    _logger.info(String.format(
                        "Job \"%s\" has been submitted with id %s",
                        job.getName(),
                        jobId
                    ));
                }
            }

        /*
         * Удаляем ран из БД, перенаправляем лог jsub и пробрасываем исключение дальше.
         */
        } catch (Exception e) {

            if (runId != 0) {
                try {
                    DAO.deleteById(JsubRun.class, runId);
                } catch (Exception daoException) {
                    
                }
            }

            Jsub.getInstance().resetLogFilepath();
            throw new BuildException(String.format(
                "Cannot queue project \"%s\": [%s] %s", _pipeline.getName(), e.getClass().getName(), e.getMessage()
            ), e);

        /*
         * Закрываем сессию. Закрытие сессии иногда (или всегда?) вызывает выброс исключения,
         * которое к моему удивлению никак не вляет на работу приложения (web-приложения по
         * крайней мере). Вообщем здесь ловим исключение и ничего не делаем. И хорошо.
         */
        } finally {
            try {
                if (session != null) {
                    session.exit();
                }
            } catch (DrmaaException e) {

            }
        }
    }
}