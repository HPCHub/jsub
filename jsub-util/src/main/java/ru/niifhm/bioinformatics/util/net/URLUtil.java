/**
 * 
 */
package ru.niifhm.bioinformatics.util.net;


import java.util.HashMap;
import java.util.Map;
import ru.niifhm.bioinformatics.util.StringPool;


/**
 * @author zeleniy
 */
public class URLUtil {


    public static String getParam(String url, String paramName) {

        return getParams(url).get(paramName);
    }


    public static Map<String, String> getParams(String url) {

        Map<String, String> result = new HashMap<String, String>();

        if (url == null) {
            return result;
        }

        int index = url.indexOf(StringPool.QUESTION);
        if (index >= 0) {
            url = url.substring(index);
        }

        String params[] = url.split("&");

        for (String param : params) {
            String temp[] = param.split("=");
            if (temp.length != 2) {
                continue;
            }

            // result.put(temp[0], java.net.URLDecoder.decode(temp[1], "UTF-8"));
            result.put(temp[0], temp[1]);
        }

        return result;
    }
}
