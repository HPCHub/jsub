/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.text.WordUtils;
import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;
import org.hibernate.criterion.Projections;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.util.StringPool;
import ru.niifhm.bioinformatics.util.io.FileUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.Preparable;


/**
 * @author zeleniy
 * If you want add one more check ability, you must add one more
 * entity in array within prepare() method and create new method
 * witn name _is<entity>Available() returns boolean.
 */
public class SystemFitnessAction implements Preparable {


    private Map<String, Boolean> _fitness = new HashMap<String, Boolean>();
    private String[] _services = {
        "bioDbFromTomcat",
        "bioDbFromNodes",
//        "activeMQ",
//        "conveyorService",
        "OGE"
    };


    public void prepare() throws Exception {

        for (String i : _services) {
            _fitness.put(i, false);
        }
    }


    public String execute() {

        for (String entity : _fitness.keySet()) {
            try {

                String methodName = String.format("_is%sAvailable", WordUtils.capitalize(entity));
                Method method = getClass().getMethod(methodName);
                boolean isAvailable = (Boolean) method.invoke(this);

                _fitness.put(entity, isAvailable);

            } catch (Exception e) {
                System.out.println(String.format("[%s] %s", e.getClass().getName(), e.getMessage()));
            }
        }

        return Action.SUCCESS;
    }


    public boolean _isOGEAvailable() {

        boolean isOGEAvailable = false;

        try {
            SessionFactory factory = SessionFactory.getFactory();
            Session session = factory.getSession();
            session.init(StringPool.EMPTY);
            session.exit();

            isOGEAvailable = true;
        } catch (DrmaaException e) {

        }

        return isOGEAvailable;
    }


    public boolean _isConveyorServiceAvailable() {

        int i = 0;

        try {
            String[] command = new String[] {"/bin/bash", "-c", "ps aux | grep [C]onveyorService"};
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(command);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            for (; reader.readLine() != null; i ++) {
                
            }
        } catch (Exception e) {
            return false;
        }

        return i == 1;
    }


    public boolean _isActiveMQAvailable() {

        File pid = new File("/opt/apache-activemq-5.7.0/data/activemq-pyxis.ripcm.com.pid");
        return pid.exists();
    }


    public boolean _isBioDbFromTomcatAvailable() {

        boolean isConnectionExists = false;

        try {
             Long count = (Long) DAO.getCriteria(JsubRun.class)
                 .setProjection(Projections.rowCount())
             .uniqueResult();

             isConnectionExists = count != null;

        } catch (Exception e) {

        }

        return isConnectionExists;
    }


    public boolean _isBioDbFromNodesAvailable() {

        boolean isConnectionExists = false;

        try {
            // Long count = (Long) DAO.getCriteria(JsubRun.class)
            // .setProjection(Projections.rowCount())
            // .uniqueResult();
            //
            // isConnectionExists = count != null;

            String status = FileUtil.read("/var/spool/biodb-check/status");
            Pattern pattern = Pattern.compile("^\\s*OK\\s*$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(status);

            isConnectionExists = matcher.matches();

        } catch (Exception e) {

        }

        return isConnectionExists;
    }


    public Map<String, Boolean> getFitness() {

        return _fitness;
    }
}