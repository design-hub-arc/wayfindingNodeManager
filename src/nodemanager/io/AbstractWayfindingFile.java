package nodemanager.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides a base for the classes used to interface
 * with the files used by the program. 
 * 
 * This class provides the functions to
 * save or load to/from local files,
 * or upload or download files from Google Drive.
 * 
 * Maybe make psuedo-static?
 * 
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
    
    public AbstractWayfindingFile(java.io.File f, FileType t){
        this(f.getName(), t);
        localCopy = f;
    }
    
    /**
     * Creates a new file on the local system in the given directory.
     * The contents of the file are given by this.getContentsToWrite()
     * @param directory the directory to save the file to.
     * @return the file created, or null if it failed
     */
    public java.io.File save(String directory){
        localCopy = new java.io.File(directory + java.io.File.separator + name + "." + type.getFileExtention());
        writeToFile(localCopy);
        return localCopy;
    }
    
    /**
     * Creates a temporary file on the local system
     * @return the newly created file, or null if it failed.
     * @throws IOException 
     */
    public final java.io.File createTemp() throws IOException{
        java.io.File temp = java.io.File.createTempFile(name, type.getFileExtention());
        temp.deleteOnExit();
        temp = save(temp.getParent());
        return temp;
    }
    
    public final com.google.api.services.drive.model.File upload(String folderName, Runnable r) throws IOException{
        driveCopy = GoogleDriveUploader.uploadFile(createTemp(), type.getMimeType(), folderName, r);
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
     * Loads this' data into the program
     */
    public final void importData() throws Exception{
        if(localCopy != null){
            try {
                readStream(new FileInputStream(localCopy));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        } else if(driveCopy != null){
            readStream(GoogleDriveUploader.download(driveCopy.getId()));
        } else {
            throw new Exception("Cannot import if neither localCopy not driveCopy have been set!");
        }
    }
    
    
    
    /**
     * Reads the contents of an InputStream,
     * then decides what to do with the content
     * @param s 
     */
    public abstract void readStream(InputStream s);
    
    /**
     * Defined in each direct subclass (AbstractCsvFile, MapFile).
     * Called by save() after a new file has been created. 
     * See the aforementioned classes for more details. 
     * @param f the file to write to.
     */
    public abstract void writeToFile(java.io.File f);
}
