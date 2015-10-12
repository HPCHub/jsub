/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import ru.niifhm.bioinformatics.jsub.PipelineLayout;


/**
 * @author zeleniy
 */
public class ExecuteCommandTest extends CommandTest {


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        _command = new ExecuteCommand();
    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.jsub.command.Command#getName()}.
     */
    @Test
    public void testGetName() {

        assertTrue("execute".equals(_command.getName()));
    }


    @Test
    public void test_execute() throws Exception {

        _command.executeCommand();
        File outputDir = _command.getProject().getOutputDir();

        /*
         * Check file existance, created while scenario executed.
         */
        File[] outputFiles = {
            new File(outputDir, "input-first-file.changed.txt"),
            new File(outputDir, "input-first-file.stage-1.result.txt"),
            new File(outputDir, "input-first-file.stage-1.second.txt"),
        };

        for (File file : outputFiles) {
            assertTrue(String.format("Cannot find \"%s\" output file", file.getPath()), file.exists());
        }

        /*
         * Check file existance, created while scenario executed.
         */
        File[] clearFiles = {
            new File(outputDir, "input-first-file.stage-1.txt"),
            new File(outputDir, "input-first-file.third.txt")
        };

        for (File file : clearFiles) {
            assertFalse(String.format("File \"%s\" exists, but must be removed", file.getPath()), file.exists());
        }


        /*
         * Inspect debug section:
         * ### debug-section ####
         * echo "input.first.file=/home/zeleniy/workspace/jsub/target/jsub-autotest/test/input-first-file.txt";
         * echo "input.second.file=/home/zeleniy/workspace/jsub/target/jsub-autotest/test/input-first-file.stage-1.txt";
         * echo "input.third.file=/home/zeleniy/workspace/jsub/target/jsub-autotest/test/input-first-file.third.txt";
         * echo "output.first.file=/home/zeleniy/workspace/jsub/target/jsub-autotest/test/input-first-file.changed.txt";
         * echo "output.second.file=/home/zeleniy/workspace/jsub/target/jsub-autotest/test/input-first-file.stage-1.second.txt";
         * echo "output.result.file=/home/zeleniy/workspace/jsub/target/jsub-autotest/test/input-first-file.stage-1.result.txt";
         * ######################
         */
        PipelineLayout layout = _command.getProject().getLayout();
        boolean isDebugSectionExists = false;
        String line;

        BufferedReader reader = new BufferedReader(new FileReader(layout.getScript(SCRIPT_STAGE_2)));
        while ((line = reader.readLine()) != null) {
            if (line.equals("### debug-section ####")) {
                isDebugSectionExists = true;

                line = reader.readLine();
                assertTrue(Pattern.matches("^echo \"input.first.file=.*input-first-file.txt\";$", line));

                line = reader.readLine();
                assertTrue(Pattern.matches("^echo \"input.second.file=.*input-first-file.stage-1.txt\";$", line));

                line = reader.readLine();
                assertTrue(Pattern.matches("^echo \"input.third.file=.*input-first-file.third.txt\";$", line));

                line = reader.readLine();
                assertTrue(Pattern.matches("^echo \"output.first.file=.*input-first-file.changed.txt\";$", line));

                line = reader.readLine();
                assertTrue(Pattern.matches("^echo \"output.second.file=.*input-first-file.stage-1.second.txt\";$", line));

                line = reader.readLine();
                assertTrue(Pattern.matches("^echo \"output.result.file=.*input-first-file.stage-1.result.txt\";$", line));

                reader.close();
                break;
            }
        }

        if (! isDebugSectionExists) {
            fail(String.format("Cannot find debug section in \"%s\"", SCRIPT_STAGE_2));
        }
    }
}