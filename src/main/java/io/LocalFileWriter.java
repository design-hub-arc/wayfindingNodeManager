package io;

import files.AbstractWayfindingFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Not sure what package this should go in.
 * 
 * @author Matt
 */
public class LocalFileWriter {
    public static File createTempFileFor(AbstractWayfindingFile awf) throws IOException{
        File temp = File.createTempFile(awf.getName(), awf.getType().getFileExtention());
        temp.deleteOnExit();
        awf.writeToFile(temp);
        return temp;
    }
    
    public static File createFileFor(AbstractWayfindingFile awf, String parentDirectory) throws IOException{
        File f = Paths.get(parentDirectory, awf.getFileName()).toFile();
        awf.writeToFile(f);
        return f;
    }
}
