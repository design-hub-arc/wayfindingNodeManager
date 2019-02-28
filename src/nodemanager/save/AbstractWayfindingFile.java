package nodemanager.save;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides a base for the classes used to interface
 * with the files used by the program. 
 * 
 * This class provides the functions to
 * save or load to/from local files,
 * or upload or download files from Google Drive.
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public abstract class AbstractWayfindingFile {
    private final String name;
    private java.io.File localCopy; // The local version of this file
    private com.google.api.services.drive.model.File driveCopy;
    private final FileType type; 
    
    public static String NL = System.getProperty("line.separator");
    
    /**
     * Creates an AbstracteWayfindingFile. Note that this does not actually do anything with files yet.
     * @param title what to call this file when it is saved or uploaded
     * @param t what type of file this will connect to. Used to get file extention and MIME type.
     */
    public AbstractWayfindingFile(String title, FileType t){
        name = title;
        localCopy = null;
        driveCopy = null;
        type = t;
    }
    
    public final java.io.File save(String directory){
        localCopy = null;
        BufferedWriter out = null;
        
        try{
            localCopy = new java.io.File(directory + java.io.File.separator + name + "." + type.getFileType());
            out = new BufferedWriter(new FileWriter(localCopy.getAbsolutePath()));
            out.write(getContentsToWrite());
            out.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        
        return localCopy;
    }
    
    public final com.google.api.services.drive.model.File upload(String folderName, boolean suppressMessages) throws IOException{
        java.io.File temp = java.io.File.createTempFile(name, type.getFileType());
        temp.deleteOnExit();
        temp = save(temp.getParent());
        
        driveCopy = GoogleDriveUploader.uploadFile(temp, type.getDriveType(), folderName, suppressMessages);
        
        return driveCopy;
    }
    
    public final com.google.api.services.drive.model.File upload(String folderName) throws IOException{
        return upload(folderName, false);
    }
    
    
    
    /**
     * Gets the contents to write to this file
     * @return what should be written to this when it is saved or uploaded
     */
    public abstract String getContentsToWrite();
    
    /**
     * Reads the contents of an InputStream,
     * then decides what to do with the content
     * @param s 
     */
    public abstract void readStream(InputStream s);
}
