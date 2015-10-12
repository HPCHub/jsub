/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.ant;


import java.io.PrintStream;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildLogger;


/**
 * @author zeleniy
 */
public class Logger implements BuildLogger {


    private static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger(Logger.class);


    /**
     * 
     */
    public Logger() {

    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#buildStarted(org.apache.tools.ant.BuildEvent)
     */
    @Override
    public void buildStarted(BuildEvent event) {

        _log.trace(_getMessage(event));
    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#buildFinished(org.apache.tools.ant.BuildEvent)
     */
    @Override
    public void buildFinished(BuildEvent event) {

        _log.trace(_getMessage(event));
    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#targetStarted(org.apache.tools.ant.BuildEvent)
     */
    @Override
    public void targetStarted(BuildEvent event) {

        _log.trace(_getMessage(event));
    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#targetFinished(org.apache.tools.ant.BuildEvent)
     */
    @Override
    public void targetFinished(BuildEvent event) {

        _log.trace(_getMessage(event));
    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#taskStarted(org.apache.tools.ant.BuildEvent)
     */
    @Override
    public void taskStarted(BuildEvent event) {

    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#taskFinished(org.apache.tools.ant.BuildEvent)
     */
    @Override
    public void taskFinished(BuildEvent event) {

    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildListener#messageLogged(org.apache.tools.ant.BuildEvent)
     */
    @Override
    public void messageLogged(BuildEvent event) {

    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildLogger#setMessageOutputLevel(int)
     */
    @Override
    public void setMessageOutputLevel(int level) {

    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildLogger#setOutputPrintStream(java.io.PrintStream)
     */
    @Override
    public void setOutputPrintStream(PrintStream output) {

    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildLogger#setEmacsMode(boolean)
     */
    @Override
    public void setEmacsMode(boolean emacsMode) {

    }


    /*
     * (non-Javadoc)
     * @see org.apache.tools.ant.BuildLogger#setErrorPrintStream(java.io.PrintStream)
     */
    @Override
    public void setErrorPrintStream(PrintStream err) {

    }


    private String _getMessage(BuildEvent event) {

        if (event.getException() != null) {
            return String.format("%s: [%s] %s", event.getTarget().getName(), event.getException(), event.getMessage());
        } else if (event.getMessage() == null) {
            return event.getTarget().getName();
        } else {
            return String.format("%s: %s", event.getTarget().getName(), event.getMessage());
        }
    }
}