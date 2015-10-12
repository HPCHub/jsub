/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/**
 * @author zeleniy
 */
public class ClearCommandTest extends CommandTest {


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        _command = new ClearCommand();
    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.jsub.command.Command#getName()}.
     */
    @Test
    public void testGetName() {

        assertTrue("clear".equals(_command.getName()));
    }


    @Test
    @Ignore
    public void test_execute() {

        fail("Not yet implemented");
    }
}