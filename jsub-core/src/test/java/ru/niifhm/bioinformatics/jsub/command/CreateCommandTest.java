/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import static org.junit.Assert.*;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import ru.niifhm.bioinformatics.jsub.Pipeline;


/**
 * @author zeleniy
 */
public class CreateCommandTest extends CommandTest {


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        _command = new CreateCommand();
    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.jsub.command.Command#getName()}.
     */
    @Test
    public void testGetName() {

        assertTrue("create".equals(_command.getName()));
    }


    @Test
    public void test_execute() {

        _command.executeCommand();

        Pipeline project = _command.getProject();

        File buildDir = project.getBuildDir();
        assertTrue(buildDir.exists());

        File configDir = new File(buildDir, Pipeline.DIRECTORY_CONFIG);
        assertTrue(configDir.exists());

        File logDir = new File(buildDir, Pipeline.DIRECTORY_LOG);
        assertTrue(logDir.exists());

        File outputDir = new File(buildDir, Pipeline.DIRECTORY_OUTPUT);
        assertTrue(outputDir.exists());

        File scriptsDir = new File(buildDir, Pipeline.DIRECTORY_SCRIPTS);
        assertTrue(scriptsDir.exists());

        File buildXMLFile = new File(configDir, "build.xml");
        assertTrue(buildXMLFile.exists());

        File buildPropertiesFile = new File(configDir, "build.properties");
        assertTrue(buildPropertiesFile.exists());
    }
}