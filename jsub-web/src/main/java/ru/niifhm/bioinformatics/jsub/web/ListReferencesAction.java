/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


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
public class ListReferencesAction {


    private int    _start  = 0;
    private int    _limit  = 25;
    private Logger _logger = Logger.getLogger(ListReferencesAction.class);


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


    public List<SeqsFile> getFiles() {

        try {

            return DAO.getCriteria(SeqsFile.class)
                .createAlias("seqsReference", "seqsReference", CriteriaSpecification.INNER_JOIN)
                .setFetchMode("seqsReference", FetchMode.JOIN)
                .addOrder(Order.desc("fileId"))
                .setFirstResult(_start)
                .setMaxResults(_limit)
                .list();

        } catch (Exception e) {
            _logger.error(String.format("Cannot load files list [%s] %s", e.getClass().getName(), e.getMessage()));
            return new ArrayList<SeqsFile>();
        }
    }
}