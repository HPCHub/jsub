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
public class PrepareCommandTest extends CommandTest {


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        _command = new PrepareCommand();
    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.jsub.command.Command#getName()}.
     */
    @Test
    public void testGetName() {

        assertTrue("prepare".equals(_command.getName()));
    }


    @Test
    public void test_execute() {

        _command.executeCommand();

        Pipeline project = _command.getProject();
        File file = new File(project.getNoDepsBuildXmlFilepath());

        assertTrue(file.exists());
    }
}