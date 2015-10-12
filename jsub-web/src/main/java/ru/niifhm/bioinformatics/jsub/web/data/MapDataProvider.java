/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;


import java.io.File;
import ru.niifhm.bioinformatics.util.StringPool;


/**
 * @author zeleniy
 */
public class MapDataProvider<E> extends FileDataProvider<E> {


    /**
     * 
     */
    public MapDataProvider(File file) {

        super(file);
    }


    public String getName() {

        String[] columns = _current.split(StringPool.TAB);

        return _nameProvider
            .setBaseName(_runName)
            .setNumber(_currentPosition)
        .getName();
    }


    public E next() {

        return current();
    }


    public E current() {

        String[] columns = _current.split(StringPool.TAB);
        return (E) columns[1];
    }
}