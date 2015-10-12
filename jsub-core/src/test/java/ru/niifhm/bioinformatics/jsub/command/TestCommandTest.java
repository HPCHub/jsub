/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import static org.junit.Assert.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.junit.Before;
import org.junit.Test;
import ru.niifhm.bioinformatics.jsub.Pipeline;


/**
 * @author zeleniy
 */
public class TestCommandTest extends CommandTest {


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {

        _command = new TestCommand();
    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.jsub.command.Command#getName()}.
     */
    @Test
    public void testGetName() {

        assertTrue("test".equals(_command.getName()));
    }


    @Test
    public void test_execute() throws Exception {

        boolean isConfigured = true;
        try {
            _command.executeCommand();
        } catch (BuildException e) {
            isConfigured = false;
        }

        assertFalse(isConfigured);
        isConfigured = true;

        Pipeline pipeline = _command.getProject();
        File buildDir = pipeline.getBuildDir();

        File file = new File(pipeline.getBuildPropertiesFilepath());
        Properties properties = new Properties();
        properties.load(new FileInputStream(file));

        File firstFile = new File(buildDir, CommandTest.INPUT_FIRST_FILE_VALUE);
        File secondFile = new File(buildDir, CommandTest.INPUT_SECOND_FILE_VALUE);

        properties.setProperty(CommandTest.INPUT_FIRST_FILE_KEY, firstFile.getAbsolutePath());
        properties.setProperty(CommandTest.INPUT_SECOND_FILE_KEY, secondFile.getAbsolutePath());

        properties.store(new FileOutputStream(file), null);

        /*
         * Здесь исключение должно вылетать по той причине, что хоть мы и
         * сконфигурили build.properties, но файлы ещё не создали.
         */
        try {
            _command.executeCommand();
        } catch (BuildException e) {
            isConfigured = false;
        }

        assertFalse(isConfigured);

        firstFile.createNewFile();
        secondFile.createNewFile();

        pipeline.load();
        _command.executeCommand();
    }
}