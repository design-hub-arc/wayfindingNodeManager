package nodemanager.save;

import java.io.BufferedWriter;
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
    private final String type; //todo change to an Enum with extention and MIME type parameters
    
    private static String NL = System.getProperty("line.separator");
    
    public AbstractWayfindingFile(String title, String mimeType){
        name = title;
        localCopy = null;
        driveId = null;
        folderName = null;
        type = mimeType;
    }
    
    //not done
    public final java.io.File save(String directory){
        localCopy = null;
        BufferedWriter out = null;
        /*
        try{
            localCopy = new java.io.File(directory + java.io.File.separator + name + "." + type);
            
        } catch(IOException ex){
            
        }*/
        
        return localCopy;
    }
    
    public final com.google.api.services.drive.model.File upload(boolean suppressMessages){
        return GoogleDriveUploader.uploadFile(localCopy, type, folderName, suppressMessages);
    }
    public final com.google.api.services.drive.model.File upload(){
        return upload(false);
    }
}
