/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.template;


/**
 * @author zeleniy
 */
public class TemplateFactory {


    /**
     * Get new template instance.
     * @param text
     * @return
     */
    public static Template newInstance(String text) {

        return new ShellTemplate(text);
    }
}