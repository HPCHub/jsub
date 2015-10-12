/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsFile;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ListReadSetsAction {


    private String _path;
    private String _type;
    private int    _start  = 0;
    private int    _limit  = 25;
    private Logger _logger = Logger.getLogger(ListReadSetsAction.class);


    public void setLimit(int limit) {

        _limit = limit;
    }


    public void setStart(int start) {

        _start = start;
    }


    public String execute() {

        return Action.SUCCESS;
    }


    public void setType(String type) {

        _type = type;
    }


    public void setPath(String path) {

        _path = path;
    }


    public long getTotalCount() {

        Criteria criteria = DAO.getCriteria(SeqsFile.class)
            .setProjection(Projections.rowCount());

        if (_type != null) {
            /*
             * Хак для fastq-файлов. Не, ну а что делать, если пайплайн для fastq файлов,
             * но в них может быть и просто fasta...
             */
            if (_type.equals("fastq")) {
                criteria.add(Restrictions.or(
                    Restrictions.or(
                        Restrictions.ilike("name", String.format(".%s", _type), MatchMode.ANYWHERE),
                        Restrictions.ilike("name", String.format(".%s", "fq"), MatchMode.ANYWHERE)
                    ),
                    Restrictions.or(
                        Restrictions.ilike("name", String.format(".%s", "fasta"), MatchMode.ANYWHERE),
                        Restrictions.ilike("name", String.format(".%s", "fna"), MatchMode.ANYWHERE)
                    )
                ));
            } else {
                criteria.add(Restrictions.ilike("name", String.format(".%s", _type), MatchMode.ANYWHERE));
            }
        }

        if (_path != null) {
            criteria.add(Restrictions.ilike("path", _path, MatchMode.ANYWHERE));
        }

        return (Long) criteria.uniqueResult();
    }


    public List<SeqsFile> getFiles() {

        try {

            Criteria criteria = DAO.getCriteria(SeqsFile.class)
                .createAlias("seqsReadSet", "seqsReadSet", CriteriaSpecification.INNER_JOIN)
                .setFetchMode("seqsReadSet", FetchMode.JOIN)
                .addOrder(Order.desc("fileId"))
                .setFirstResult(_start)
                .setMaxResults(_limit);

            if (_type != null) {
                if (_type.equals("fastq")) {
                    criteria.add(Restrictions.or(
                        Restrictions.or(
                            Restrictions.ilike("name", String.format(".%s", _type), MatchMode.ANYWHERE),
                            Restrictions.ilike("name", String.format(".%s", "fq"), MatchMode.ANYWHERE)
                        ),
                        Restrictions.or(
                            Restrictions.ilike("name", String.format(".%s", "fasta"), MatchMode.ANYWHERE),
                            Restrictions.ilike("name", String.format(".%s", "fna"), MatchMode.ANYWHERE)
                        )
                    ));
                } else {
                    criteria.add(Restrictions.ilike("name", String.format(".%s", _type), MatchMode.ANYWHERE));
                }
            }

            if (_path != null) {
                criteria.add(Restrictions.ilike("path", _path, MatchMode.ANYWHERE));
            }

            return criteria.list();

        } catch (Exception e) {
            _logger.error(String.format("Cannot load files list [%s] %s", e.getClass().getName(), e.getMessage()));
            return new ArrayList<SeqsFile>();
        }
    }
}