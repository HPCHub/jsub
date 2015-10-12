/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * Jsub application layout.
 * Класс предоставляет доступ к ресурсам приложения. Готовое приложение в
 * смысле организации его в файловой системе может находится в виде:<br />
 * - набора файлов и папок<br />
 * - jar-архива (CLI версия приложения)<br />
 * - war-архива<br />
 * В таком случае внутренние ресурсы приложения нуждаются в различных способах
 * доступа, что и решается в рамках текущего и дочерних классов:<br />
 * - {@link ExplodedLayout}<br />
 * - {@link JarLayout}
 * @author zeleniy
 */
public abstract class Layout {


    public static final String     STATE_EXPLODED       = "exploded";
    public static final String     STATE_JAR            = "jar";
    public static final String     DIRECTORY_LOG        = "log";
    public static final String     DIRECTORY_TMP        = "tmp";
    public static final String     DIRECTORY_CONFIG     = "config";
    public static final String     FILE_TOOLS_XML       = "tools.xml";
    public static final String     FILE_JSUB_PROPERTIES = "jsub.properties";
    /**
     * Jsub instance.
     */
    private static volatile Layout _instance;
    private String                 _applicationDirectory;


    /**
     * Get jsub instance.
     * @return
     */
    public static Layout getInstance() {

        if (_instance == null) {
            synchronized (Layout.class) {
                if (_instance == null) {
                    if (getPackageType().equals(STATE_JAR)) {
                        _instance = new JarLayout();
                    } else {
                        _instance = new ExplodedLayout();
                    }
                }
            }
        }

        return _instance;
    }


    /**
     * 
     */
    protected Layout() {

        /*
         * Reslove application directory.
         */
        String path = getApplicationContainer();
        if (path.endsWith(".jar") || path.endsWith(".war")) {
            _applicationDirectory = new File(path).getParent() + "/";
        } else if (path.contains("WEB-INF")) {
            _applicationDirectory = new StringBuilder(path.substring(0, path.indexOf("WEB-INF")))
                .append("WEB-INF")
                .append(File.separator)
                .append("classes")
                .append(File.separator)
            .toString();
        } else {
            _applicationDirectory = path;
        }
    }


    /**
     * Get jsub current layout state.
     * @return
     */
    public static String getPackageType() {

        String path = getApplicationContainer();
        if (path.endsWith(".jar") || path.endsWith(".war")) {
            return STATE_JAR;
        } else {
            return STATE_EXPLODED;
        }
    }


    /**
     * List classes of package.
     * @param packageName package name
     * @return
     * @throws Exception
     */
    abstract public Class<?>[] getPackageClasses(String packageName) throws Exception;


    /**
     * Get application container.
     * It may be some directory or jar file.
     * @return
     */
    public static String getApplicationContainer() {

        return Layout.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }


    /**
     * Get application log directory.
     * @return
     */
    public String getLogDirectory() {

        return getDirectory(DIRECTORY_LOG);
    }


    /**
     * Get application config directory.
     * @return
     */
    public String getConfigDirectory() {

        return getDirectory(DIRECTORY_CONFIG);
    }


    public String getConfigFile(String file) {

        return getFile(getDirectory(DIRECTORY_CONFIG), file);
    }


    /**
     * Get directory path within jsub installation.
     * @param directory
     * @return
     */
    public String getDirectory(String directory) {

        return new StringBuilder(getApplicationDirectory())
            .append(directory)
            .append(File.separator)
            .toString();
    }


    /**
     * Get file path within some directory of jsub installation.
     * @param directory
     * @param file
     * @return
     */
    public String getFile(String directory, String file) {

        return directory + file;
    }


    /**
     * Get application log file.
     * @return
     */
    public String getApplicationLogFile() {

        return new StringBuilder(getLogDirectory())
            .append("jsub.log")
            .toString();
    }


    /**
     * Get application directory.
     * @return
     */
    public String getApplicationDirectory() {

        return _applicationDirectory;
    }


    /**
     * Get application temporary directory.
     * @return
     */
    public String getTmpDirectory() {

        return getDirectory(DIRECTORY_TMP);
    }


    /**
     * Get application temporary file.
     * Method return full path to file in temporary directory.
     * @param file
     * @return
     */
    public String getTmpFile(String file) {

        return new StringBuilder(getTmpDirectory())
            .append(file)
            .toString();
    }


    /**
     * Get a file from resource directory as File.
     * @param resourceName
     * @return
     * @throws FileNotFoundException
     */
    public File getResourceAsFile(String resourceName) throws FileNotFoundException {

        return _getResourceAsFile(resourceName);
    }


    /**
     * Get a file from config directory as File.
     * @param configName
     * @return
     * @throws FileNotFoundException
     */
    public File getConfigAsFile(String configName) throws FileNotFoundException {

        return _getResourceAsFile(configName);
    }


    /**
     * Get a resource file as File.
     * @param resourceName
     * @return
     * @throws FileNotFoundException
     */
    private File _getResourceAsFile(String resourceName) throws FileNotFoundException {

        try {
            URL resource = getResource(resourceName);
            File file = new File(resource.toURI());
            return file;
        } catch (URISyntaxException e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }


    /**
     * Get application resource URL.
     * @param resourceName
     * @return
     */
    public URL getResource(String resourceName) {

        return getClass().getClassLoader().getResource(resourceName);
    }


    /**
     * Get a file from resource directory as InputStream.
     * @param resourceName
     * @return
     * @throws FileNotFoundException
     */
    public InputStream getResourceAsInputStream(String resourceName) throws FileNotFoundException {

        return _getResourceAsInputStream(resourceName);
    }


    /**
     * Get a file from config directory as InputStream.
     * @param configName
     * @return
     * @throws FileNotFoundException
     */
    public InputStream getConfigAsInputStream(String configName) throws FileNotFoundException {

        return _getResourceAsInputStream(configName);
    }


    /**
     * Get a resource file as InputStream.
     * @param resourceName
     * @return
     * @throws FileNotFoundException
     */
    private InputStream _getResourceAsInputStream(String resourceName) throws FileNotFoundException {

        return getClass().getResourceAsStream(File.separator + resourceName);
    }
}