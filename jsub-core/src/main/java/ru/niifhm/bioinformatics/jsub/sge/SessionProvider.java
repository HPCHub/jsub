/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.sge;


import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;
import ru.niifhm.bioinformatics.util.StringPool;


/**
 * Класс, обеспечивающий доступ к сессии планировщика SGE.
 * Сессия должна быть одна http://gridscheduler.sourceforge.net/javadocs/org/ggf/drmaa/Session.html#init(java.lang.String)
 * Иначе крах. А именно не удаcтся запустить очередной процесс, если не выполнился предыдущий. В данном случае
 * не удасться запустить второй, пока не выполнился первый т.к. мы ждём окончания работы Session.TIMEOUT_WAIT_FOREVER.
 * Поэтому объект сесси хранится здесь один и всегда.
 * @author zeleniy
 */
public class SessionProvider {


    private static volatile Session _session;


    public static Session getSession() throws DrmaaException {

        if (_session == null) {
            synchronized (SessionProvider.class) {
                if (_session == null) {
                    SessionFactory factory = SessionFactory.getFactory();
                    _session = factory.getSession();
                    _session.init(StringPool.EMPTY);
                }
            }
        }

        return _session;
    }
}