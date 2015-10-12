/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ListRunsAction {


    private String _query;
    private int    _start = 0;
    private int    _limit = 25;


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

        return DAO.getCount(SeqsRun.class);
    }


    public List<SeqsRun> getRuns() {

        Criteria criteria = DAO.getCriteria(SeqsRun.class)
            .addOrder(Order.asc("name"))
            .setFirstResult(_start)
            .setMaxResults(_limit);

        if (_query != null) {
            criteria.add(Restrictions.ilike("name", _query, MatchMode.START));
        }

        return criteria.list();
    }


    public void setQuery(String query) {

        _query = query;
    }
}