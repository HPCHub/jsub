/**
 * 
 */
package ru.niifhm.bioinformatics.util.net;


import static org.junit.Assert.*;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @author zeleniy
 */
@RunWith(JUnitParamsRunner.class)
public class URLUtilTest {


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

    }


    /**
     * Test method for {@link ru.niifhm.bioinformatics.util.net.URLUtil#getParams(String)}.
     */
    @Test
    @Parameters
    public void testGetParams(String url, int number) {

        Map<String, String> params = URLUtil.getParams(url);
        assertEquals(number, params.size());
    }


    private Object[][] parametersForTestGetParams() {

        return new Object[][] {
            {null, 0},
            {"http://www.qwerty.com/about", 0},
            {"/about?a=b&x=y", 2},
            {"http://www.qwerty.com/about?a=b&x=y", 2},
            {"a=b&x=y", 2},
            {"?a=b&x=y", 2}
        };
    }
}