/**
 * 
 */
package ru.niifhm.bioinformatics.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;


/**
 * @author zeleniy
 */
public class Package {


    private Package() {

    }


    public static Class<?>[] getPackageClasses(String packageName) throws ClassNotFoundException, IOException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            dirs.add(new File(resources.nextElement().getFile()));
        }

        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(_findClasses(directory, packageName));
        }

        return classes.toArray(new Class[classes.size()]);
    }


    public static Class<?>[] getPackageClasses(String jarName, String packageName) throws ClassNotFoundException, IOException {

        List<Class<?>> classes = new ArrayList<Class<?>>();
        packageName = packageName.replace('.', '/');

        JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
        JarEntry jarEntry;

        while ((jarEntry = jarFile.getNextJarEntry()) != null) {
            if ((jarEntry.getName().startsWith(packageName)) && (jarEntry.getName().endsWith(".class"))) {
                String className = jarEntry.getName().replace('/', '.').substring(0, jarEntry.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }

        return classes.toArray(new Class[classes.size()]);
    }


    private static List<Class<?>> _findClasses(File directory, String packageName) throws ClassNotFoundException {

        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (! directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(_findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes
                    .add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }

        return classes;
    }
}