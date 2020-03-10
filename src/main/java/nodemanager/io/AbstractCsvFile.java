package nodemanager.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Serves as the base class for the node files:
 * <ul>
 * <li>NodeCoordFile</li>
 * <li>NodeConnFile</li>
 * <li>NodeLabelFile</li>
 * </ul>
 * as well as the manifest
 * @author Matt Crow
 */
public abstract class AbstractCsvFile extends AbstractWayfindingFile{
    public AbstractCsvFile(String title, FileType t){
        super(title, t);
    }
    
    @Override
    public final void writeToFile(File f){
        BufferedWriter out = null;
        try{
            out = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
            out.write(getContentsToWrite());
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Gets the contents to write to this file
     * @return what should be written to this when it is saved or uploaded
     */
    public abstract String getContentsToWrite();
}