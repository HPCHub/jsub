/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;



/**
 * @author zeleniy
 *
 */
public class NumberNameProvider extends NameProvider {


    /* (non-Javadoc)
     * @see ru.niifhm.bioinformatics.jsub.web.data.NameProvider#getName()
     */
    @Override
    public String getName() {

        return String.format("%s_%s", _name, _number);
    }
}