package files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

/**
 * The AbstractWayfindingFile class is used to 
 * serve as the base for the various data files
 * created by the program, and used by the Wayfinding 
 * program.
 * 
 * Subclasses must include the proper fields to
 * contain their appropriate data, as this class
 * contains mostly just methods for creating files,
 * with abstract methods for reading from and writing to files.
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public abstract class AbstractWayfindingFile {
    private final String name;
    private final FileType type; 
    
    /**
     * Creates an AbstracteWayfindingFile. Note that this does not actually
     * create any files on the user's hard drive yet.
     * 
     * @param title what to call this file when it is saved or uploaded to the drive
     * @param t what type of file this will connect to. Used to get file extention and MIME type.
     */
    public AbstractWayfindingFile(String title, FileType t){
        name = title;
        type = t;
    }
    
    /**
     * Creates an instance of a subtype of AbstractWayfindingFile based of
     * of the given FileType
     * 
     * @param name what to name the new file
     * @param t the file type to create.
     * @return an object inheriting from AbstractWayfindingFile
     */
    public static AbstractWayfindingFile fromType(String name, FileType t){
        AbstractWayfindingFile ret = null;
        switch(t){
            case NODE_COORD:
                ret = new NodeCoordFile(name);
                break;
            case NODE_CONN:
                ret = new NodeConnFile(name);
                break;
            case LABEL:
                ret = new NodeLabelFile(name);
                break;
            case MAP_IMAGE:
                ret = new MapFile(name);
                break;
            case MANIFEST:
                ret = new WayfindingManifest(name);
                break;
            case VERSION_LOG:
                ret = new VersionLog();
                break;
            default:
                throw new UnsupportedOperationException("Cannot decode file from type " + t);
        }
        return ret;
    }
    
    public final String getName(){
        return name;
    }
    
    public final FileType getType(){
        return type;
    }

    /**
     * 
     * @return the name of this file, plus its file extension. 
     */
    public final String getFileName(){
        return name + "." + type.getFileExtention();
    }
    
    public final File createTempFile() throws IOException{
        File temp = File.createTempFile(name, type.getFileExtention());
        temp.deleteOnExit();
        writeToFile(temp);
        return temp;
    }
    
    public final File createFile(String parentDirectory) throws IOException{
        File f = Paths.get(parentDirectory, getFileName()).toFile();
        writeToFile(f);
        return f;
    }
    
    /**
     * Reads the contents of an InputStream,
     * then sets the contents of this file
     * @param s the inputstream from either a local file, or the Google Drive
     * 
     * @throws java.io.IOException if any errors occur when reading the stream
     */
    public abstract void setContents(InputStream s) throws IOException;
    
    /**
     * Writes the contents of this to a file on the user's computer.
     *  
     * @param f the file to write to.
     * @throws java.io.IOException if an error occurs
     */
    public abstract void writeToFile(File f) throws IOException;
    
    /**
     * Exports the current state of the program
     * into this file's contents
     */
    public abstract void exportData();
    
    /**
     * Imports the data from this file into
     * the program
     */
    public abstract void importData();
}
