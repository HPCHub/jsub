/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import static org.junit.Assert.*;
import java.io.File;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.job.Job;


/**
 * @author zeleniy
 */
public class InstallCommandTest extends CommandTest {


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        _command = new InstallCommand();
    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.jsub.command.Command#getName()}.
     */
    @Test
    public void testGetName() {

        assertTrue("install".equals(_command.getName()));
    }


    @Test
    public void test_execute() {

        _command.executeCommand();

        Pipeline project = _command.getProject();
        List<Job> jobs = project.getJobList();

        for (Job job : jobs) {
            assertTrue(new File(job.getInstaller().getBashScriptPath()).exists());
        }
    }
}