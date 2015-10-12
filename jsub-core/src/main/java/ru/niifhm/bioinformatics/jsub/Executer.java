package ru.niifhm.bioinformatics.jsub;


import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRunFile;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;
import ru.niifhm.bioinformatics.jsub.ant.Property;


/**
 * Абстрактный класс Executer.
 * Класс определяет интерфейс, который должен реализовывать любой
 * дочерний класс, который хочет предоставить альтернативный метод
 * вычисления/исполнения проекта jsub. Ключевой метод {@link #execute()}
 * вызывается на последнем этапе жизненного цикла проекта. Вызов метода
 * должен обеспечить правильное и последовательное выполнения bash-скриптов
 * в директории scripts/ проекта.
 * @author zeleniy
 */
abstract public class Executer {


    /**
     * Минимальное количество ядер необходимых для выполнения таргета.
     */
    public static final int MINIMUM_CORE_NUMBER = 1;
    /**
     * Максимальное количество ядер необходимых для выполнения таргета.
     * Расчитывется как количество доступных ядер на текущем хосте минус 4.
     * Каждый скрипт не занимает все ядра на узле, а -4 по причине образования
     * "пробок" в планировщике. Это сделано для того, чтобы тяжёлые мультиядерные
     * задачи не забивали собой весь пул узлов кластера, а оставляли место для
     * лёгких задач выполняющихся в один поток.
     */
    //!!! обязательно убрать комментарий для рабочих кластеров - для jsub-test оставить "20"
    public static final int MAXIMUM_CORE_NUMBER = 20;//Runtime.getRuntime().availableProcessors() - 4;
    private static Logger   _logger             = Logger.getLogger(Executer.class);
    /**
     * Ссылка на исполняемый проект.
     */
    protected Pipeline      _pipeline;


    /**
     * @param project проект для исполнения.
     */
    public Executer(Pipeline project) {

        _pipeline = project;
    }


    /**
     * Execute project.
     * @throws BuildException
     */
    abstract public void execute() throws BuildException;


    /**
     * Регистрация попытки исполнения проекта в БД.
     * @return
     */
    public long registerRun() {

        JsubRun run = new JsubRun();

        try {
            run.setProjectType(_pipeline.getType());
            run.setProjectName(_pipeline.getName());
            run.setProjectTag(_pipeline.getTag());
            run.setProjectDirectory(_pipeline.getDirectory());
            run.setUserEmail(_pipeline.getEmail());
            run.setSystemUserLogin(System.getProperty("user.name"));
            run.setStartDate(new Date());
            DAO.save(run);

            for (Property property : _pipeline.getUserDefinedFiles()) {

                SeqsFile seqsFile = (SeqsFile) DAO.getCriteria(SeqsFile.class)
                    .add(Restrictions.eq("path", property.getValue()))
                .uniqueResult();

                JsubRunFile jsubRunFile = new JsubRunFile(run);
                if (seqsFile != null) {
                    jsubRunFile.setFileId(seqsFile.getFileId());
                }

                jsubRunFile.setPathname(property.getValue());
                DAO.save(jsubRunFile);
            }
        } catch (Exception e) {
            _logger.error(String.format(
                "Cannot register jsub run in DB [%s] %s", e.getClass().getName(), e.getMessage()
            ));
        }

        return run.getRunId();
    }


    /**
     * Получить количество необходимых ядер для выполнения программы tool.
     * Метод работает с классом {@link Tools}, который в свою очередь представляет
     * доступ к конфигурационному файлу tools.*.xml.
     * Для програм выполняющихся в один поток возвращается {@link #MINIMUM_CORE_NUMBER},
     * для многопоточных - {@link #MAXIMUM_CORE_NUMBER}.
     * @param tool название программы.
     * @return
     */
    public static int getRequiredCoreNumber(String tool) {

        /*
         * TODO: What about cluster configuration file ?
         */
    	System.out.println("MAXIMUM_CORE_NUMBER  " + MAXIMUM_CORE_NUMBER);
    	System.out.println("MINIMUM_CORE_NUMBER  " + MINIMUM_CORE_NUMBER);
    	System.out.println("MAXIMUM_CORE_NUMBER = Runtime.getRuntime().availableProcessors() - 4 =  "+ Runtime.getRuntime().availableProcessors() + "- 4");
    	_logger.error(String.format("MAXIMUM_CORE_NUMBER  ", MAXIMUM_CORE_NUMBER));
    	_logger.error(String.format("MINIMUM_CORE_NUMBER  ", MINIMUM_CORE_NUMBER));
    	_logger.error(String.format("MAXIMUM_CORE_NUMBER = Runtime.getRuntime().availableProcessors() - 4 =  ", Runtime.getRuntime().availableProcessors(), "- 4"));
    	if (tool == null) {
            return MINIMUM_CORE_NUMBER;
        }

        if (Tools.getInstance().isToolMultiprocessor(tool)) {
            return MAXIMUM_CORE_NUMBER;
        } else {
            return MINIMUM_CORE_NUMBER;
        }
    }
}