/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;


/**
 * @author zeleniy
 */
public class ReadSetNameProvider extends NameProvider {


    public String getName() {

        String sampleName  = _readSet.getSequence().getSeqsSample().getName();
        String projectName = String.format("%s_%s_%s", _name, sampleName, _number);

        return projectName;
    }
}