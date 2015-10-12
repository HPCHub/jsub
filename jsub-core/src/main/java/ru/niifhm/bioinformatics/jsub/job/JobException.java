/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.job;


/**
 * Jsub common exception class.
 * @author zeleniy
 */
public class JobException extends Exception {


    /**
     * 
     */
    public JobException() {

    }


    /**
     * @param message
     */
    public JobException(String message) {

        super(message);
    }


    /**
     * @param cause
     */
    public JobException(Throwable cause) {

        super(cause);
    }


    /**
     * @param message
     * @param cause
     */
    public JobException(String message, Throwable cause) {

        super(message, cause);
    }
}