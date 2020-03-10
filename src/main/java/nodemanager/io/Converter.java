package nodemanager.io;

import localFiles.NodeLabelFile;
import localFiles.NodeCoordFile;
import localFiles.NodeConnFile;
import localFiles.MapFile;
import localFiles.FileType;
import localFiles.AbstractWayfindingFile;

/**
 * May switch to using this in 
 * lieu of individual classes,
 * since I am using most of the
 * wayfinding files as static classes
 * in a way.
 * 
 * @author Matt Crow
 */
public class Converter {
    public static AbstractWayfindingFile convert(com.google.api.services.drive.model.File f, FileType t){
        AbstractWayfindingFile ret = null;
        
        switch(t){
            case NODE_COORD:
                ret = new NodeCoordFile();
                break;
            case NODE_CONN:
                ret = new NodeConnFile();
                break;
            case LABEL:
                ret = new NodeLabelFile();
                break;
            case MAP_IMAGE:
                ret = new MapFile();
                break;
            default:
                System.out.println("Not supported in Importer.convert: " + t.getTitle());
                break;
        }
        
        return ret;
    }
    
    public static AbstractWayfindingFile convert(java.io.File f, FileType t){
        AbstractWayfindingFile ret = null;
        
        switch(t){
            case NODE_COORD:
                ret = new NodeCoordFile();
                break;
            case NODE_CONN:
                ret = new NodeConnFile();
                break;
            case LABEL:
                ret = new NodeLabelFile();
                break;
            case MAP_IMAGE:
                ret = new MapFile();
                break;
            default:
                System.out.println("Not supported in Importer.convert: " + t.getTitle());
                break;
        }
        
        if(ret != null){
            ret.setLocalCopy(f);
        }
        
        return ret;
    }
}