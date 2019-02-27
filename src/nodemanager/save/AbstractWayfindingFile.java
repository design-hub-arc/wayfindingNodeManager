package nodemanager.save;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Provides a base for the classes used to interface
 * with the files used by the program. 
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public abstract class AbstractWayfindingFile {
    
    private final String name;
    private java.io.File localCopy; // The local version of this file
    private String driveId;         // The fileId of this' copy in the google drive
    private String folderName;      // The name of the folder this' copy is in in the google drive
    private final FileType type; 
    
    public static String NL = System.getProperty("line.separator");
    
    public AbstractWayfindingFile(String title, FileType t){
        name = title;
        localCopy = null;
        driveId = null;
        folderName = null;
        type = t;
    }
    
    public abstract void writeContents(BufferedWriter buff) throws IOException;
    
    public final java.io.File save(String directory){
        localCopy = null;
        BufferedWriter out = null;
        
        try{
            localCopy = new java.io.File(directory + java.io.File.separator + name + "." + type.getFileType());
            out = new BufferedWriter(new FileWriter(localCopy.getAbsolutePath()));
            writeContents(out);
            out.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        
        return localCopy;
    }
    
    public final com.google.api.services.drive.model.File upload(boolean suppressMessages){
        return GoogleDriveUploader.uploadFile(localCopy, type.getDriveType(), folderName, suppressMessages);
    }
    public final com.google.api.services.drive.model.File upload(){
        return upload(false);
    }
}
