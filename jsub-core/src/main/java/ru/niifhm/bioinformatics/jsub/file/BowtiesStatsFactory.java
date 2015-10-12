/**
 * 
 */
package ru.niifhm.bioinformatics.jsub.file;


import java.io.File;


/**
 * @author zeleniy
 */
public class BowtiesStatsFactory {


    public static BowtieStats getInstance(File directory) {

        if (directory.getPath().contains("-long-")) {
            return new Bowtie2Stats(directory);
        } else {
            return new BowtieStats(directory);
        }
    }
}