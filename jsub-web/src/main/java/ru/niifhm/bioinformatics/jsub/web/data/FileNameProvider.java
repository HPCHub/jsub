/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.web.data;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import ru.niifhm.bioinformatics.util.StringPool;


/**
 * @author zeleniy
 */
public class FileNameProvider extends NameProvider {


    private BufferedReader _reader;


    public FileNameProvider(File file) {

        try {
            _reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {

        }
    }


    public String getName() {

        try {
            return _reader.readLine();
        } catch (IOException e) {
            return StringPool.BLANK;
        }
    }
}