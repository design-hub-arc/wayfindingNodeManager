package nodemanager.io;

/**
 * Used to keep track of 
 * several things pertaining
 * to the files used by wayfinding
 * 
 * @author Matt Crow
 */
public enum FileType {
    NODE_COORD( "Node coordinates", "NodeCoords", "csv", "text/csv"),
    NODE_CONN(  "Node connections", "NodeConn",   "csv", "text/csv"),
    LABEL(      "labels",           "Labels",     "csv", "text/csv"),
    MAP_IMAGE(  "map image",        "MapImage",   "png", "image/png"),
    MANIFEST(   "NONE",             "Manifest",   "csv", "text/csv"),
    VERSION_LOG("NONE",             "NONE",       "csv", "text/csv");
    
    private final String TITLE;
    private final String SUFFIX;
    private final String FILE_EXTENTION;
    private final String MIME_TYPE;
    
    private FileType(String title, String suffix, String extention, String mimeType){
        TITLE = title;
        SUFFIX = suffix;
        FILE_EXTENTION = extention;
        MIME_TYPE = mimeType;
    }
    
    public String getTitle(){
        return TITLE;
    }
    
    public String getSuffix(){
        return SUFFIX;
    }
    
    public String getFileExtention(){
        return FILE_EXTENTION;
    }
    
    public String getMimeType(){
        return MIME_TYPE;
    }
}
