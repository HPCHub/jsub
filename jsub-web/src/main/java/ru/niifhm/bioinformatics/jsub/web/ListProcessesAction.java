/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.util.List;
import org.hibernate.criterion.Order;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.jsub.ProcessList;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * @author zeleniy
 */
public class ListProcessesAction extends ActionSupport {


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

        return DAO.getCount(JsubRun.class);
    }


    public List<JsubRun> getProcesses() {

        List<JsubRun> runs = DAO.getCriteria(JsubRun.class)
            .addOrder(Order.desc("runId"))
            .setFirstResult(_start)
            .setMaxResults(_limit)
        .list();

//        int index = -1;
//        List<JsubRun> processes = ProcessList.getInstance().getProcesses();
//        for (JsubRun run : runs) {
//            if ((index = processes.indexOf(run)) >= 0) {
//                run.setCurrentJob(processes.get(index).getCurrentJob());
//            }
//        }

        return runs;
    }
}