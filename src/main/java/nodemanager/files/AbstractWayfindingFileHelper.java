package nodemanager.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import nodemanager.model.Graph;

/**
 * The AbstractWayfindingFileHelper class is used to 
 * serve as the base for helping read and write the various data files
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
public abstract class AbstractWayfindingFileHelper {
    private final String name;
    private final FileType type; 
    
    /**
     * Creates an AbstracteWayfindingFile. Note that this does not actually
     * create any files on the user's hard drive yet.
     * 
     * @param title what to call this file when it is saved or uploaded to the drive
     * @param t what type of file this will connect to. Used to get file extention and MIME type.
     */
    public AbstractWayfindingFileHelper(String title, FileType t){
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
    public static AbstractWayfindingFileHelper fromType(String name, FileType t){
        AbstractWayfindingFileHelper ret = null;
        switch(t){
            case NODE_COORD:
                ret = new NodeCoordFileHelper(name);
                break;
            case NODE_CONN:
                ret = new NodeConnFileHelper(name);
                break;
            case LABEL:
                ret = new NodeLabelFileHelper(name);
                break;
            case MAP_IMAGE:
                ret = new MapFileHelper(name);
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
    
    public final File writeToTempFile(Graph g) throws IOException{
        File temp = File.createTempFile(name, type.getFileExtention());
        temp.deleteOnExit();
        this.writeGraphDataToFile(g, new FileOutputStream(temp));
        return temp;
    }
    
    public final File writeToFileUnderParent(Graph g, String parentDirectory) throws IOException{
        File f = Paths.get(parentDirectory, getFileName()).toFile();
        this.writeGraphDataToFile(g, new FileOutputStream(f));
        return f;
    }
        
    /**
     * Reads this file type's data from the given InputStream, and adds it to
     * the given graph.
     * 
     * @param g
     * @param in
     * @throws IOException 
     */
    public abstract void readGraphDataFromFile(Graph g, InputStream in) throws IOException;
    
    /**
     * Writes the contents of the given Graph relevant to this file type to the
     * given OutputStream
     * 
     * @param g
     * @param out
     * @throws IOException 
     */
    public abstract void writeGraphDataToFile(Graph g, OutputStream out) throws IOException;   
}