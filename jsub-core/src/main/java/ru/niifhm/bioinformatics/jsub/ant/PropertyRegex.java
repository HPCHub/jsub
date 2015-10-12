/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.ant;


import org.apache.tools.ant.Task;
import ru.niifhm.bioinformatics.jsub.template.DefaultTepmlate;


/**
 * @author zeleniy
 */
public class PropertyRegex extends Property {


    /**
     * @param task
     */
    public PropertyRegex(Task task) {

        super(
            (String) task.getRuntimeConfigurableWrapper().getAttributeMap().get("property"),
            (String) task.getRuntimeConfigurableWrapper().getAttributeMap().get("input"),
            task
        );
    }


    public String getInput() {

         String input = (String) _task.getRuntimeConfigurableWrapper().getAttributeMap().get("input");
         if (DefaultTepmlate.isPlaceholder(input)) {
             return getName(input);
         } else {
             return input;
         }
    }


    public String getOutput() {

        String output = (String) _task.getRuntimeConfigurableWrapper().getAttributeMap().get("property");
        if (DefaultTepmlate.isPlaceholder(output)) {
            return getName(output);
        } else {
            return output;
        }
    }
}