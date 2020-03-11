package files;

import java.io.IOException;
import java.io.InputStream;
import nodemanager.io.VersionLog;

/**
 * Provides a base for the classes used to interface
 * with the files used by the program. 
 * 
 * It provides the generic template for the files used
 * by the Wayfinding program. Note that these are not
 * specifically files on the local file system or Google Drive.
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public abstract class AbstractWayfindingFile {
    private final String name;
    private final FileType type; 
    
    public static String NL = System.getProperty("line.separator");
    
    /**
     * Creates an AbstracteWayfindingFile. Note that this does not actually do anything with files yet.
     * @param title what to call this file when it is saved or uploaded
     * @param t what type of file this will connect to. Used to get file extention and MIME type.
     */
    public AbstractWayfindingFile(String title, FileType t){
        name = title;
        type = t;
    }
    
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
    
    public String getName(){
        return name;
    }
    
    public final FileType getType(){
        return type;
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
     * Imports the data from this file into
     * the program
     */
    public abstract void importData();
    
    /**
     * Writes the contents of this to a file on the user's computer.
     *  
     * @param f the file to write to.
     * @throws java.io.IOException if an error occurs
     */
    public abstract void writeToFile(java.io.File f) throws IOException;
}
