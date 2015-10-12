/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import ru.niifhm.bioinformatics.jsub.Config;
import ru.niifhm.bioinformatics.jsub.Jsub;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.util.io.FileUtil;


/**
 * @author zeleniy
 */
@RunWith(Suite.class)
@SuiteClasses({
    CreateCommandTest.class,
    GenerateCommandTest.class,
    TestCommandTest.class,
    GraphCommandTest.class,
    PrepareCommandTest.class,
    InstallCommandTest.class,
    AssembleCommandTest.class,
    CopyCommandTest.class,
    ExecuteCommandTest.class,
    QueueCommandTest.class,
    ClearCommandTest.class
})
public class CommandTestSuite {


    public static final String PROPERTY_PROJECT_TYPE = "jsub-autotest";
    public static final String PROPERTY_PROJECT_NAME = "test";
    public static final String PROPERTY_PROJECT_TAG  = "tagDirectory";
    public static final String PROPERTY_PROJECT_DIR  = "target";


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        Config configuration = Config.getInstance();
        configuration.set(Config.FLAG_SKIP_COPY_PHASE, true);
        configuration.set(Config.FLAG_LOG_STDOUT, false);
        configuration.set(Config.MODE_DEBUG, true);
        configuration.set(Config.PROPERTY_MODE, "create");
        configuration.set(Config.PROPERTY_CLEAR_INPUT, "autotest-stage-1,autotest-stage-2");

        Jsub jsub = Jsub.getInstance();

        Pipeline project = Pipeline.newInstance(
            PROPERTY_PROJECT_NAME,
            PROPERTY_PROJECT_TAG,
            PROPERTY_PROJECT_TYPE,
            PROPERTY_PROJECT_DIR
        );

        FileUtil.delete(project.getBuildDir());
    }


    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }
}