package ru.niifhm.bioinformatics.jsub.command;


import static org.junit.Assert.*;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.util.Package;
import ru.niifhm.bioinformatics.util.RandomUtil;


@Ignore
public class CommandTest {


    public static final String SCRIPT_STAGE_1          = "autotest-stage-1.sh";
    public static final String SCRIPT_STAGE_2          = "autotest-stage-2.sh";
    public static final String INPUT_FIRST_FILE_KEY    = "input.first.file";
    public static final String INPUT_FIRST_FILE_VALUE  = "input-first-file.txt";
    public static final String INPUT_SECOND_FILE_KEY   = "input.second.file";
    public static final String INPUT_SECOND_FILE_VALUE = "input-second-file.txt";
    public static final String INPUT_THIRD_FILE        = "input-third-file.txt";
    protected Command          _command;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }


    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }


    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {

        _command = null;
    }


    @Test
    public void testFactory() throws Exception {

        List<Class<Command>> classes = _getCommands();
        for (Class<Command> clazz : classes) {
            Command command = Command.factory(_getCommandName(clazz));
            assertTrue(command instanceof Command);
        }
    }


    private List<Class<Command>> _getCommands() throws ClassNotFoundException, IOException {

        List<Class<Command>> classes = new ArrayList<Class<Command>>();
        Class<?>[] packageClasses = Package.getPackageClasses("ru.niifhm.bioinformatics.jsub.command");
        for (Class<?> clazz : packageClasses) {
            if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()) ||
                ! clazz.getName().endsWith("Command")) {
                continue;
            }

            classes.add((Class<Command>) clazz);
        }

        return classes;
    }


    private String _getCommandName(Class<Command> clazz) {

        String className = clazz.getName();
        return className.substring(className.lastIndexOf(".") + 1, className.length() - "Command".length());
    }


    @Ignore
    @Test
    public void testExecute() {

        fail("Not yet implemented");
    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.jsub.command.Command#getProject()}.
     */
    @Test
    public void test_getProject() {

        Pipeline project = _command.getProject();
        assertTrue(project instanceof Pipeline);
    }


    @Test(expected = UnsupportedOperationException.class)
    public void testGetDependencies() throws Exception {

        List<Command> dependencies = _command.getDependencies();
        assertNotNull(dependencies);
        dependencies.add(_getRandomCommand());
    }


    private Command _getRandomCommand() throws Exception {

        List<Class<Command>> classes = _getCommands();
        int random = RandomUtil.getInt(0, classes.size() - 1);

        return Command.factory(_getCommandName(classes.get(random)));
    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.jsub.command.Command#executeCommand()}.
     */
    @Test
    @Ignore
    public void testExecuteCommand() {

        fail("Not yet implemented");
    }


    @Test
    public void testAddDependency() throws Exception {

        List<Class<Command>> classes = _getCommands();
        int count = _command.getDependencies().size();

        for (int i = 0; i < classes.size(); i ++) {
            _command._addDependency(_getRandomCommand());
            assertEquals( ++ count, _command.getDependencies().size());
        }
    }
}