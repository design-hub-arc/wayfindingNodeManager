package nodemanager.save;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Provides a base for the classes used to interface
 * with the files used by the program. 
 * @author Matt Crow (greengrappler12@gmail.com)
 * 
 * add ability to download / read file using this
 */
public abstract class AbstractWayfindingFile {
    
    private final String name;
    private java.io.File localCopy; // The local version of this file
    private String driveId;         // The fileId of this' copy in the google drive
    private final FileType type; 
    
    public static String NL = System.getProperty("line.separator");
    
    public AbstractWayfindingFile(String title, FileType t){
        name = title;
        localCopy = null;
        driveId = null;
        type = t;
    }
    
    
    /**
     * Gets the contents to write to this file
     * @return 
     */
    public abstract String getContents();
    
    public final java.io.File save(String directory){
        localCopy = null;
        BufferedWriter out = null;
        
        try{
            localCopy = new java.io.File(directory + java.io.File.separator + name + "." + type.getFileType());
            out = new BufferedWriter(new FileWriter(localCopy.getAbsolutePath()));
            out.write(getContents());
            out.close();
        } catch(IOException ex){
            ex.printStackTrace();
        }
        
        return localCopy;
    }
    
    public final com.google.api.services.drive.model.File upload(String folderName, boolean suppressMessages) throws IOException{
        java.io.File temp = java.io.File.createTempFile(name, type.getFileType());
        temp.deleteOnExit();
        System.out.println(temp.getParent());
        temp = save(temp.getParent());
        System.out.println(temp.getAbsoluteFile());
        return GoogleDriveUploader.uploadFile(temp, type.getDriveType(), folderName, suppressMessages);
    }
    public final com.google.api.services.drive.model.File upload(String folderName) throws IOException{
        return upload(folderName, false);
    }
}
