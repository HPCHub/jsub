/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.command;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.tools.ant.BuildException;
import ru.niifhm.bioinformatics.jsub.sge.LogFilenameFilter;
import ru.niifhm.bioinformatics.util.TimeUtil;
import ru.niifhm.bioinformatics.util.io.FileUtil;


/**
 * @author zeleniy
 */
public class StatCommand extends Command implements CLICommand {


    /**
     * Command description.
     */
    private static final String DESCRIPTION = "get project time statistics";
    private static final Pattern SCRIPTS_UPTIME_PATTERN = Pattern.compile("uptime: (\\d+) seconds");


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.CLICommand#getDescription()
     */
    public String getDescription() {

        return DESCRIPTION;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.command.Command#_execute()
     */
    @Override
    protected void _execute() throws BuildException {

        File[] files = getProject()
            .init()
            .getLogDirectory()
        .listFiles(new LogFilenameFilter());

        Arrays.sort(files, NameFileComparator.NAME_COMPARATOR);

        for (File file : files) {
            try {
                Matcher fileMatcher = LogFilenameFilter.SCRIPTS_LOG_FILES_PATTERN.matcher(file.getName());
                fileMatcher.find();

                String name = fileMatcher.group(1);
                String id = fileMatcher.group(2);

                String log = FileUtil.read(file);
                Matcher textMatcher = SCRIPTS_UPTIME_PATTERN.matcher(log);

                String message;
                if (textMatcher.find()) {
                    message = String.format("%s [%s] %s", name, id, TimeUtil.format(Integer.valueOf(textMatcher.group(1))));
                } else {
                    message = String.format("%s [%s]", name, id);
                }

                System.out.println(message);
            } catch (IOException e) {
                
            }
        }
    }
}