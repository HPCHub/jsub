/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;


import org.apache.commons.io.FilenameUtils;

/**
 * @author zeleniy
 */
public class ReadSetNameProvider extends NameProvider {


    public String getName() {

        String projectName;

        try {
            String sampleName = _readSet.getSequence().getSeqsSample().getName();
            projectName = String.format("%s_%s_%s", _name, sampleName, _number);
        } catch (NullPointerException _) {
            String sampleName = FilenameUtils.getBaseName(_readSet.getSequence().getSeqsFile().getPath());
            projectName = String.format("%s_%s", _name, sampleName);
        }

        return projectName;
    }
}