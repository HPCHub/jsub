/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;


import java.io.File;
import java.util.*;

import org.apache.log4j.Logger;
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

    private static Logger _logger = Logger.getLogger(ReadSetDataProvider.class);

    /**
     * 
     */
    private List<ReadSet> _readSets;
    /**
     * 
     */
    protected ReadSet     _current;

    public List<ReadSet> getReadSets() {
        return _readSets;
    }

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

            _readSets = new ArrayList<ReadSet>();
            if (fasta != null) {
                File fasta_dir = new File(fasta.getValue());
                if (fasta_dir.isDirectory()) {
                    String[] fasta_paths = fasta_dir.list();
                    Arrays.sort(fasta_paths);
                    if (quality == null) {
                        for (String p : fasta_paths)
                            _readSets.add(new ReadSet(fasta_dir + File.separator + p, null));
                    } else {
                        File quality_dir = new File(quality.getValue());
                        if (!quality_dir.isDirectory()) {
                            throw new Exception("If fasta is a directory than quality must be a directory either!");
                        }
                        String[] quality_paths = quality_dir.list();
                        if (fasta_paths.length != quality_paths.length) {
                            throw new Exception("fasta and quality directories should contain the same number of files!");
                        }
                        Arrays.sort(quality_paths);
                        for (int i = 0; i < fasta_paths.length; i++) {
                            _readSets.add(new ReadSet(fasta_dir + File.separator + fasta_paths[i], quality_dir + File.separator + quality_paths[i]));
                        }
                    }
                } else {
                    _readSets.add(new ReadSet(fasta.getValue(), quality == null ? null : quality.getValue()));
                }
            }
        }

        return this;
    }
}
