/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import ru.niifhm.bioinformatics.jsub.Pipeline;
import ru.niifhm.bioinformatics.jsub.job.Job;
import ru.niifhm.bioinformatics.jsub.template.Template;
import ru.niifhm.bioinformatics.jsub.template.TemplateFactory;
import ru.niifhm.bioinformatics.util.io.FileUtil;



/**
 * @author zeleniy
 *
 */
public class AssembleCommandTest extends CommandTest {


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        _command = new AssembleCommand();
    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.jsub.command.Command#getName()}.
     */
    @Test
    public void testGetName() {

        assertTrue("assemble".equals(_command.getName()));
    }


    @Test
    public void test_execute() {

        _command.executeCommand();

        Pipeline project = _command.getProject();
        for (Job job : project.getJobList()) {
            try {
                String script = FileUtil.read(job.getInstaller().getBashScriptPath());
                Template template = TemplateFactory.newInstance(script);

                /*
                 * Last job finalize.py replace placeholder ${project.run.id}
                 * on execute stage, not assemble.
                 */
                if (! project.getLastJob().getName().equals(job.getName())) {
                    assertTrue(
                        String.format("script template placeholders still available for job \"%s\"", job.getName()),
                        template.isEmpty()
                    );
                } else {
                    assertEquals(1, template.getPlaceholders().size());
                }
            } catch (IOException e) {
                fail(String.format("Cannot read executable script text for \"%s\" job", job.getName()));
            }
        }
    }
}