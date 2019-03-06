package nodemanager.io;

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
    
    /**
     * Creates a new file on the local system in the given directory.
     * The contents of the file are given by this.getContentsToWrite()
     * @param directory the directory to save the file to.
     * @return the file created, or null if it failed
     */
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
    
    /**
     * Creates a temporary file on the local system
     * @return the newly created file, or null if it failed.
     * @throws IOException 
     */
    public final java.io.File createTemp() throws IOException{
        java.io.File temp = java.io.File.createTempFile(name, type.getFileType());
        temp.deleteOnExit();
        temp = save(temp.getParent());
        return temp;
    }
    
    public final com.google.api.services.drive.model.File upload(String folderName, Runnable r) throws IOException{
        driveCopy = GoogleDriveUploader.uploadFile(createTemp(), type.getDriveType(), folderName, r);
        return driveCopy;
    }
    
    public final com.google.api.services.drive.model.File upload(String folderName) throws IOException{
        return upload(folderName, ()->{});
    }
    
    public String getUrl() throws NullPointerException{
        String ret = "";
        if(driveCopy == null){
            throw new NullPointerException(name + " hasn't been uploaded to the drive yet, so I can't get its URL");
        } else {
            ret = "https://drive.google.com/uc?export=download&id=" + driveCopy.getId();
        }
        
        return ret;
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
