/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * @author zeleniy
 */
public class JarLayout extends Layout {


    public Class<?>[] getPackageClasses(String packageName) throws Exception {

        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String packagePath = packageName.replaceAll("\\.", "/");

        List<Class<?>> classes = new ArrayList<Class<?>>();

        try {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(path));
            JarEntry jarEntry;
            while ((jarEntry = jarFile.getNextJarEntry()) != null) {

                String entryName = jarEntry.getName();
                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    String className = entryName.substring(0, entryName.length() - 6).replaceAll("/", "\\.");
                    classes.add(Class.forName(className));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classes.toArray(new Class[classes.size()]);
    }
}