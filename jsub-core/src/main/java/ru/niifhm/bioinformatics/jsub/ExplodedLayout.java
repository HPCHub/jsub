/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;



/**
 * @author zeleniy
 *
 */
public class ExplodedLayout extends Layout {


    public Class<?>[] getPackageClasses(String packageName) throws Exception {

        return ru.niifhm.bioinformatics.util.Package.getPackageClasses(packageName);
    }
}