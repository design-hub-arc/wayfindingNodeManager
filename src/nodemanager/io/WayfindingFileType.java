package nodemanager.io;

/**
 * Used to keep track of 
 * several things pertaining
 * to the files used by wayfinding
 * 
 * @author Matt Crow
 */
public enum WayfindingFileType {
    NODE_COORD("Node coordinates", "NodeCoords"),
    NODE_CONN("Node connections", "NodeConn"),
    LABEL("labels", "Labels"),
    MAP_IMAGE("map image", "MapImage"),
    MANIFEST("NONE", "Manifest");
    
    private final String TITLE;
    private final String SUFFIX;
    
    private WayfindingFileType(String title, String suffix){
        TITLE = title;
        SUFFIX = suffix;
    }
    
    public String getTitle(){
        return TITLE;
    }
    
    public String getSuffix(){
        return SUFFIX;
    }
}
