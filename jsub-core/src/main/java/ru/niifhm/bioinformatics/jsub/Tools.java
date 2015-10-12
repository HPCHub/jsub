/**
 * 
 */
package ru.niifhm.bioinformatics.jsub;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import ru.niifhm.bioinformatics.jsub.configuration.XmlConfigurationLoader;


/**
 * Класс Tools предоставляет доступ к данным конфигурационного файла 
 * tools.*.xml. При инициализации объект класса считывает tools.*.xml 
 * в хэш. Ключи хэша представляют собой название тэга + "." + название
 * аттрибута. Рассмотрим пример:<p> 
 * {@code
 * <bowtie path="${bowtie}" thread="--threads" />
 * }</p>
 * При обработке вышеописанного xml-элемента в хэш {@link #_tools}
 * будет положено два элемента:<br />
 * - bowtie.path<br />
 * - bowtie.thread<br />
 * с соответствующими значениями.
 * @author zeleniy
 */
public class Tools {


    /**
     * Configuration class instance.
     */
    private static volatile Tools _instance;
    private static final Logger _logger = Logger.getLogger(Tools.class);
    /**
     * tools.xml hash map representation.
     */
    private Map<String, String>   _tools;
    /**
     * Available tools names list.
     */
    private List<String>          _names;


    /**
     * Get class single instance.
     * @return
     */
    public static Tools getInstance() {

        if (_instance == null) {
            synchronized (Tools.class) {
                if (_instance == null) {
                    _instance = new Tools();
                }
            }
        }

        return _instance;
    }


    /**
     * Private constructor.
     */
    private Tools() {

        /*
         * Получаем путь к конфиг-му файлу.
         */
        String filename = Config.getInstance().getEnvironmentFileName(Layout.FILE_TOOLS_XML);

        /*
         * Загружаем конфиг-ый файл.
         */
        try {
            _tools = new XmlConfigurationLoader(
                Layout.getInstance().getResourceAsInputStream(filename)
            ).getConfiguration();
            _logger.error(String.format( "!!! Config file name !!!", filename));
            System.out.println("!!! Config file name !!! "+ filename);
        } catch (Exception e) {
            _logger.error(String.format(
                "Cannot load \"%s\" [%s]: %s", filename, e.getClass().getName(), e.getMessage()
            ));
        }
    }


    /**
     * Get available tools names list.
     * @return
     */
    public List<String> getToolsNames() {

        /*
         * Прокручиваем в цикле все элементы _tools, вырезаем
         * из них имена и укладываем ровными стопками в _names.
         */
        if (_names == null) {
            _names = new ArrayList<String>();
            for (String property : _tools.keySet()) {
                /*
                 * TODO: тут наверное можно/нужно использовать родной getNormalizedName()..?
                 * Без тестов менять сейчас ничего не хочу.
                 */
                String name = property.substring(0, property.indexOf("."));
                if (! _names.contains(name)) {
                    _names.add(name);
                }
            }
        }

        return _names;
    }


    /**
     * Метод возвращает имя тулсы по ключу из хэша {@link #_tools}.
     * Например, преобразует "bowtie.path" в "bowtie".
     * Если говорить строго, метод возвращает все символы строки
     * string с первого по первое вхождение точки.
     * @param string входящая строка.
     * @return
     */
    public String getNormalizedName(String string) {

        return string.substring(0, string.indexOf("."));
    }


    /**
     * Is tool multiprocessor?
     * @param toolName
     * @return
     */
    public boolean isToolMultiprocessor(String toolName) {

        return _tools.get(toolName + ".thread") != null;
    }


    /**
     * Get tool path.
     * @param toolName
     * @return
     */
    public String getToolPath(String toolName) {

        return _tools.get(toolName + ".path");
    }


    /**
     * Get tools.xml hash map representation.
     * @return
     */
    public Map<String, String> getConfiguration() {

        return _tools;
    }
}