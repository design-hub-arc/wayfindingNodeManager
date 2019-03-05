package nodemanager.io;

/**
 * Used by AbstractWayfindingFile to convert file extentions 
 * to MIME types used by Google Drive
 * @author Matt Crow
 */
public enum FileType {
    CSV("csv", "text/csv"),
    PNG("png", "image/png");
    
    private final String extention;
    private final String driveType;
    
    private FileType(String fileType, String mimeType){
        extention = fileType;
        driveType = mimeType;
    }
    
    public String getFileType(){
        return extention;
    }
    
    public String getDriveType(){
        return driveType;
    }
}
