/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.ReadSet;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsReadSet;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRunsFile;
import ru.niifhm.bioinformatics.jsub.Properties;
import ru.niifhm.bioinformatics.jsub.ant.Property;
import ru.niifhm.bioinformatics.jsub.configuration.XConfig;


/**
 * @author zeleniy
 */
public class ReadSetDataProvider<E> extends DataProvider<E> {


    /**
     * 
     */
    private List<ReadSet> _readSets;
    /**
     * 
     */
    protected ReadSet     _current;


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#current()
     */
    public E current() {

        return (E) _current;
    }


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#getProperties()
     */
    public Map<String, String> getProperties() {

        Map<String, String> properties = new HashMap<String, String>();

        ReadSet readSet = _current;

        properties.put(_mapToInput(readSet.getSequencePath()), readSet.getSequencePath());
        String qualityPath = readSet.getQualityPath();
        if (qualityPath != null) {
            properties.put(_mapToInput(readSet.getQualityPath()), readSet.getQualityPath());
        }

        return properties;
    }


    /**
     * @param path
     * @return
     */
    private String _mapToInput(String path) {

        if (path == null) {
            return null;
        }

        List<String> parts = Arrays.asList(path.split("\\."));

        for (Map.Entry<String, String> map : XConfig.getInstance().getExtensionsToProperties().entrySet()) {
            if (parts.contains(map.getKey())) {
                return String.format("input.%s", map.getValue());
            }
        }

        return null;
    }


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#getName()
     */
    public String getName() {

        return _nameProvider
            .setReadSet(_current)
            .setBaseName(_runName)
            .setNumber(_currentPosition)
        .getName();
    }


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#getCount()
     */
    public int getCount() {

        return _readSets.size();
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#hasNext()
     */
    @Override
    public boolean hasNext() {

        if (_currentPosition >= _readSets.size()) {
            return false;
        } else {
            return true;
        }
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#next()
     */
    @Override
    public E next() {

        _current = _readSets.get(_currentPosition);
        _currentPosition ++;

        return (E) _current;
    }


    /*
     * (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#remove()
     */
    @Override
    public void remove() {

        // TODO Auto-generated method stub
    }


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.DataProvider#_init()
     */
    public DataProvider<E> init() throws Exception {

        if (_readSets != null) {
            return this;
        }

        if (_runId > 0) {
            List<Long> fileIds = DAO.getCriteria(SeqsRunsFile.class)
                .setProjection(Projections.property("fileId"))
                .add(Restrictions.eq("seqsRun.runId", _runId))
                .list();

            List<SeqsReadSet> seqsReadSets = DAO.getCriteria(SeqsReadSet.class)
                .createAlias("seqsFile", "seqsFile", CriteriaSpecification.LEFT_JOIN)
                .createAlias("seqsSample", "seqsSample", CriteriaSpecification.LEFT_JOIN)
                .add(Restrictions.in("seqsFile.fileId", fileIds))
                .list();

            _readSets = new ArrayList<ReadSet>();
            for (SeqsReadSet readSet : seqsReadSets) {
                if (! readSet.isQuality()) {
                    _readSets.add(new ReadSet(readSet, seqsReadSets));
                }
            }
        } else {
            Properties props = new Properties(_properties);

            Property fasta = props.getLikeName(new String[] {"fasta", "fastq", "fna", "fa"}, true);
            Property quality = props.getLikeName("qual", true);

            if (fasta == null) {
                _readSets = new ArrayList<ReadSet>();
            } else if (fasta != null && quality != null) {
                _readSets = Arrays.asList(new ReadSet(fasta.getValue(), quality.getValue()));
            } else {
                _readSets = Arrays.asList(new ReadSet(fasta.getValue()));
            }
        }

        return this;
    }
}