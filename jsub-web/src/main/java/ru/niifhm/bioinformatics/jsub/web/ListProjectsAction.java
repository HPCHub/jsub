/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.util.List;
import org.hibernate.criterion.Order;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsProject;
import com.opensymphony.xwork2.Action;


/**
 * @author zeleniy
 */
public class ListProjectsAction {


    private int _start = 0;
    private int _limit = 25;


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

        return DAO.getCount(SeqsProject.class);
    }


    public List<SeqsProject> getProjects() {

        return DAO.getCriteria(SeqsProject.class)
            .addOrder(Order.desc("createDate"))
            .setFirstResult(_start)
            .setMaxResults(_limit)
        .list();
    }
}