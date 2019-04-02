package nodemanager.io;

import com.google.api.services.drive.model.File;
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
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public abstract class AbstractWayfindingFile {
    private String name;
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
    
    public final String getDriveId() throws NullPointerException{
        return driveCopy.getId();
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
     * Associates a drive file as the drive copy of this file.
     * @param f the file to associate with this.
     */
    public final void setDriveCopy(com.google.api.services.drive.model.File f){
        driveCopy = f;
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
     * @param folderName the name of the folder on the google drive to upload to.
     * @return a DriveIOOp. See its file to see what it does
     */
    public final DriveIOOp<File> upload(String folderName){
        /*
        java.io.File upload = localCopy;
        if(upload == null){
            try {
                upload = createTemp();
                localCopy = upload;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }*/
        return GoogleDriveUploader.uploadFile(this, folderName).addOnSucceed((f)->{
            setDriveCopy((File)f);
        });
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
