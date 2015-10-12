/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web;


import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import ru.niifhm.bioinformatics.biodb.DAO;
import ru.niifhm.bioinformatics.biodb.msdba.JsubRun;
import ru.niifhm.bioinformatics.biodb.msdba.SeqsRun;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


/**
 * List jsub tags action.
 * @author zeleniy
 */
public class ListTagsAction extends ActionSupport {


    /**
     * Available tags set.
     */
    private Set<String> _tags = new TreeSet<String>();
    /**
     * Class logger.
     */
    private Logger      _logger = Logger.getLogger(ListTagsAction.class);


    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.ActionSupport#execute()
     */
    @SuppressWarnings("unchecked")
    public String execute() {

        try {
            List<String> runsNames = DAO.getCriteria(JsubRun.class)
                .setProjection(Projections.distinct(Projections.property("projectTag")))
                .add(Restrictions.isNotNull("projectTag"))
            .list();

            List<String> existedTags = DAO.getCriteria(SeqsRun.class)
                .setProjection(Projections.distinct(Projections.property("name")))
            .list();

            _tags.addAll(runsNames);
            _tags.addAll(existedTags);

        } catch (Exception e) {
            _logger.error(String.format("Cannot list tags [%s] %s", e.getClass().getName(), e.getMessage()));
            return Action.ERROR;
        }

        return Action.SUCCESS;
    }


    /**
     * Get tags set.
     * @return
     */
    public Set<String> getTags() {

        return _tags;
    }
}