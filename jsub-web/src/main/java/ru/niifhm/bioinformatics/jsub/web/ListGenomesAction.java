/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ListGenomesAction {


    private int    _start  = 0;
    private int    _limit  = 25;
    private Logger _logger = Logger.getLogger(ListGenomesAction.class);


    public void setLimit(int limit) {

        _limit = limit;
    }


    public void setStart(int start) {

        _start = start;
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public long getTotalCount() {

        return (Long) DAO.getCriteria(SeqsFile.class)
            .createAlias("seqsReference", "seqsReference", CriteriaSpecification.INNER_JOIN)
            .setFetchMode("seqsReference", FetchMode.JOIN)
            .setProjection(Projections.rowCount())
            .uniqueResult();
    }


    public List<File> getFiles() {

        List<File> files = new ArrayList<File>();

        try {

            List<SeqsFile> seqsFiles = DAO.getCriteria(SeqsFile.class)
                .createAlias("seqsReference", "seqsReference", CriteriaSpecification.INNER_JOIN)
                .setFetchMode("seqsReference", FetchMode.JOIN)
                .addOrder(Order.desc("fileId"))
                .setFirstResult(_start)
                .setMaxResults(_limit)
                .list();

            for (SeqsFile seqsFile : seqsFiles) {
                files.add(new File(seqsFile.getPath() + ".genome"));
            }

        } catch (Exception e) {
            _logger.error(String.format("Cannot load files list [%s] %s", e.getClass().getName(), e.getMessage()));
        }

        return files;
    }
}
