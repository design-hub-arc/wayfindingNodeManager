package nodemanager.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * May switch to using this in 
 * lieu of individual classes,
 * since I am using most of the
 * wayfinding files as static classes
 * in a way.
 * 
 * @author Matt Crow
 */
public class Importer {
    public static boolean importFile(File f, FileType type){
        boolean success = true;
        System.out.println("importing " + f.getAbsolutePath());
        try{
            switch(type){
                case NODE_COORD:
                    new NodeCoordFile().readStream(new FileInputStream(f));
                    break;
                case NODE_CONN:
                    new NodeConnFile().readStream(new FileInputStream(f));
                    break;
                case LABEL:
                    new NodeLabelFile().readStream(new FileInputStream(f));
                    break;
                case MAP_IMAGE:
                    new MapFile().readStream(new FileInputStream(f));
                    break;
                case MANIFEST:
                    WayfindingManifest m = new WayfindingManifest("");
                    m.readStream(new FileInputStream(f));
                    m.unpack();
                    break;
                default:
                    System.err.println("Type not supported in Importer.importFile: " + type.name());
                    success = false;
                    break;
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            success = false;
        }
        
        return success;
    }
    
    /**
     * Loads a wayfinding file into the program.
     * 
     * @param f
     * @param type
     * @return 
     */
    public static boolean importFile(AbstractWayfindingFile f, FileType type){
        boolean success = true;
        
        try{
            f.importData();
        } catch(Exception e){
            success = false;
            e.printStackTrace();
        }
        
        return success;
    }
    
    public static AbstractWayfindingFile convert(java.io.File f, FileType t){
        AbstractWayfindingFile ret = null;
        
        switch(t){
            case NODE_COORD:
                ret = new NodeCoordFile(f);
                break;
            case NODE_CONN:
                ret = new NodeConnFile(f);
                break;
            case LABEL:
                ret = new NodeLabelFile(f);
                break;
            case MAP_IMAGE:
                ret = new MapFile(f);
                break;
            default:
                System.out.println("Not supported in Importer.convert: " + t.getTitle());
                break;
        }
        
        return ret;
    }
}
