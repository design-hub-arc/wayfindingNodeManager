package nodemanager.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * creates a manifest file containing all the currently active data,
 * then upload each file to the google drive,
 * export them to the web,
 * and paste their URLs into the manifest
 * @author Matt Crow
 * 
 * 
 * TODO: make this able to save a local copy?
 */
public class WayfindingManifest extends AbstractWayfindingFile{
    private final String prefix;
    private final String inDriveFolder;
    private final HashMap<String, String> urls;
    
    public WayfindingManifest(String folderName){
        super(folderName + "Manifest", FileType.CSV);
        prefix = folderName;
        inDriveFolder = folderName;
        urls = new HashMap<>();
    }
    
    private final void populate(){
        com.google.api.services.drive.model.File googleFile = null;
        try{
            googleFile = new NodeCoordFile(prefix).upload(inDriveFolder);
            urls.put("Node coordinates", "https://drive.google.com/uc?export=download&id=" + googleFile.getId());
        } catch(IOException ex){
            ex.printStackTrace();
        }
        
        try{
            googleFile = new NodeConnFile(prefix).upload(inDriveFolder);
            urls.put("Node connections", "https://drive.google.com/uc?export=download&id=" + googleFile.getId());
        } catch(IOException ex){
            ex.printStackTrace();
        }
        
        try{
            googleFile = new NodeLabelFile(prefix).upload(inDriveFolder);
            urls.put("labels", "https://drive.google.com/uc?export=download&id=" + googleFile.getId());
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public String getContentsToWrite() {
        populate();
        StringBuilder sb = new StringBuilder("Data, URL");
        for(Map.Entry<String, String> entry : urls.entrySet()){
                sb
                        .append(NL)
                        .append(entry.getKey())
                        .append(", ")
                        .append(entry.getValue());
            }
        return sb.toString();
    }

    @Override
    public void readStream(InputStream s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
