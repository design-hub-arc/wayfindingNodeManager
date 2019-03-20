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
}
