package localFiles;

import com.google.api.services.drive.model.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import nodemanager.io.DriveIOOp;
import nodemanager.io.GoogleDriveUploader;

/**
 * Provides a base for the classes used to interface
 * with the files used by the program. 
 * 
 * This class provides the functions to
 * save or load to/from local files,
 * or upload or download files from Google Drive.
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public abstract class AbstractWayfindingFile {
    private String name;
    private java.io.File localCopy; // The local version of this file
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
        type = t;
    }
    
    public final FileType getType(){
        return type;
    }
    
    /**
     * Gets the File to upload.
     * Returns this' local copy, if it exists,
     * otherwise, returns a temporary file.
     * @return 
     */
    public final java.io.File getUpload(){
        java.io.File upload = localCopy;
        if(upload == null){
            try{
                upload = createTemp();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
        return upload;
    }
    
    /**
     * Associates a local file as the local copy of this file.
     * @param f the file to associate with this.
     */
    public final void setLocalCopy(java.io.File f){
        localCopy = f;
        name = f.getName();
    }
    
    /**
     * Saves this' contents to the local file system.
     * If this has a local file associated with it,
     * writes to that file,
     * otherwise, creates a new file on the local system in the given directory and writes to it.
     * The contents of the file are given by this.getContentsToWrite()
     * @param directory the directory to save the file to.
     * @return the file created or updated
     */
    public java.io.File save(String directory){
        if(localCopy == null){
            localCopy = new java.io.File(directory + java.io.File.separator + name + "." + type.getFileExtention());
        }
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
    
    /**
     * Uploads this' local copy to the drive. 
     * If a local copy isn't set, 
     * creates a temporary file to hold the data from the program,
     * then uploads that temporary file.
     * 
     * @param folderId the id of the folder on the google drive to upload to.
     * @return a DriveIOOp. See its file to see what it does
     */
    public DriveIOOp<File> upload(String folderId){
        return GoogleDriveUploader.uploadFile(this, folderId);
    }
    
    /**
     * Loads this' data into the program
     * @return a DriveIOOp containing this' data. 
     * @throws java.lang.Exception if neither 
     * this' local nor drive copies have been set
     */
    public final DriveIOOp<InputStream> importData() throws Exception{
        DriveIOOp<InputStream> ret = null;
        if(localCopy != null){
            try {
                readStream(new FileInputStream(localCopy));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        } else if(driveCopy != null){
            ret = GoogleDriveUploader
                    .download(driveCopy.getId())
                    .addOnSucceed((stream)->readStream(stream));
        } else {
            throw new Exception("Cannot import if neither localCopy not driveCopy have been set!");
        }
        return ret;
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