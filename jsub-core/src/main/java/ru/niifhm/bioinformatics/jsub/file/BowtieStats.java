/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.file;


import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;


/**
 * @author zeleniy
 */
public class BowtieStats {


    private float               _humanMappingpercent;
    private float               _refernceMappingpercent;
    protected File              _directory;
    private static final Logger _logger  = Logger.getLogger(BowtieStats.class);
    private Pattern             _pattern = Pattern.compile(
        "# reads with at least one reported alignment: \\d+ \\((\\d+(?:\\.\\d+))%\\)"
    );



    public BowtieStats(File directory) {

        _directory = directory;
    }


    public float getHumanMappingPercent() {

        if (_humanMappingpercent == 0.0F) {
            try {
                File file = new File(_directory, Files.BOWTIE_STATS_HUMAN_MAPPING);
                List<String> lines = IOUtils.readLines(new FileInputStream(file));

                for (String line : lines) {
                    Matcher matcher = _pattern.matcher(line);
                    if (matcher.matches()) {
                        _humanMappingpercent = Float.parseFloat(matcher.group(1));
                    }
                }
            } catch (Exception e) {
                _logger.error(String.format(
                    "Cannot sense human papping percent [%s] %s", e.getClass().getName(), e.getMessage()
                ));
            }
        }

        return _humanMappingpercent;
    }


    public float getReferenceMappingPercent() {

        if (_refernceMappingpercent == 0.0F) {
            try {
                File file = new File(_directory, Files.BOWTIE_STATS_REFERENCE_MAPPING);
                List<String> lines = IOUtils.readLines(new FileInputStream(file));

                for (String line : lines) {
                    Matcher matcher = _pattern.matcher(line);
                    if (matcher.matches()) {
                        _refernceMappingpercent = Float.parseFloat(matcher.group(1));
                    }
                }
            } catch (Exception e) {
                _logger.error(String.format(
                    "Cannot sense human papping percent [%s] %s", e.getClass().getName(), e.getMessage()
                ));
            }
        }

        return _refernceMappingpercent;
    }
}