/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.util.StringPool;


/**
 * @author zeleniy
 */
public class ProcessList {


    /**
     * Reporting file.
     */
    private File                        _file;
    /**
     * Timer for repeatedly reporting file parsing.
     */
    private Timer                       _timer;
    /**
     * ProcessList class singleton instance.
     */
    private static volatile ProcessList _instance;
    /**
     * Web-server system user login.
     * TODO: may be it is not good ? Move it to config...
     */
    private String                      _systemUserLogin = "tomcat";
    /**
     * Current process list.
     */
    private Map<String, JsubRun>        _processes       = new HashMap<String, JsubRun>();
    /**
     * Class logger.
     */
    private static Logger               _logger          = Logger.getLogger(ProcessList.class);
    /**
     * Reporting file pattern.
     * See http://arc.liv.ac.uk/SGE/htmlman/htmlman5/reporting.html for file format details.
     */
    private Pattern                     _pattern         = Pattern.compile(
        "\\d+:job_log:.*:([^=]+)=([^=]+)=([^=]+)=([^:]+):" + _systemUserLogin + ":.*:(.*)"
    );


    /**
     * Get ProcessList singleton instance.
     * @return
     */
    public static ProcessList getInstance() {

        if (_instance == null) {
            synchronized (ProcessList.class) {
                if (_instance == null) {
                    _instance = new ProcessList();
                }
            }
        }

        return _instance;
    }


    /**
     * Constructor.
     */
    private ProcessList() {

        _file = new File(Config.getInstance().get(Config.GRID_REPORTING_FILE));
        if (! _file.exists()) {
            _logger.error(String.format("Grid log file \"%s\" does not exists ", _file.getPath()));
        }

        _timer = new Timer();
        _timer.schedule(new Loader(), 0, 5 * 1000);
    }


    /**
     * Get current processes list.
     * @return
     */
    public List<JsubRun> getProcesses() {

        return new ArrayList<JsubRun>(_processes.values());
    }


    /**
     * @author zeleniy
     * Loader class.
     * It is starts in separate tread and repeatedly parse SGE reporting file.
     */
    class Loader extends TimerTask {


        public void run() {

            try {
                if (! _file.exists()) {
                    throw new Exception(String.format("Grid log file \"%s\" does not exists ", _file.getPath()));
                }

                DataInputStream inputStream = new DataInputStream(new FileInputStream(_file));
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    Matcher matcher = _pattern.matcher(line);
                    if (matcher.matches()) {

                        String type = matcher.group(1);
                        String name = matcher.group(2);
                        String job = matcher.group(3);
                        String email = matcher.group(4);
                        String status = matcher.group(5);
                        String key = type + StringPool.EQUAL  + name;

                        if (status.contains("deleted")) {
                            _processes.remove(key);
                            continue;
                        }

                        JsubRun run = new JsubRun();
                        run.setProjectType(type);
                        run.setProjectName(name);
                        run.setCurrentJob(job);
                        run.setUserEmail(email);
                        run.setSystemUserLogin(_systemUserLogin);

                        _processes.put(key, run);
                    }
                }

                inputStream.close();
            } catch (Exception e) {
                _logger.warn(String.format(
                    "Cannot load processlist from \"%s\" [%s] %s",
                    _file.getParent(),
                    e.getClass().getName(),
                    e.getMessage()
                    ));
            }
        }
    }
}