package nodemanager.save;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
 * make this extend abstractwayfindingfile
 * TODO: make this able to save a local copy?
 */
public class WayfindingManifest {
    private final String prefix;
    private final String title;
    private final String inDriveFolder;
    private final HashMap<String, String> urls;
    
    public WayfindingManifest(String folderName){
        title = folderName + "manifest.csv";
        prefix = folderName;
        inDriveFolder = folderName;
        urls = new HashMap<>();
    }
    
    public void populate(String path){
        com.google.api.services.drive.model.File googleFile = null;
        try{
            googleFile = new NodeCoordFile(prefix).upload(inDriveFolder, true);
            urls.put("Node coordinates", "https://drive.google.com/uc?export=download&id=" + googleFile.getId());
        } catch(IOException ex){
            ex.printStackTrace();
        }
        
        try{
            googleFile = new NodeConnFile(prefix).upload(inDriveFolder, true);
            urls.put("Node connections", "https://drive.google.com/uc?export=download&id=" + googleFile.getId());
        } catch(IOException ex){
            ex.printStackTrace();
        }
        
        try{
            googleFile = new NodeLabelFile(prefix).upload(inDriveFolder, true);
            urls.put("labels", "https://drive.google.com/uc?export=download&id=" + googleFile.getId());
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }
    
    public File export(String path){
        File f = null;
        BufferedWriter out = null;
        String nl = System.getProperty("line.separator");
        
        String time = new SimpleDateFormat("MM_dd_yyyy").format(Calendar.getInstance().getTime());
        
        populate(path);
        
        try {
            f = new File(path + File.separator + "wayfindingManifest" + time + ".csv");
            
            out = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
            out.write("Data, URL" + nl);
            
            for(Map.Entry<String, String> entry : urls.entrySet()){
                out.write(entry.getKey() + ", " + entry.getValue() + nl);
            }
            
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return f;
    }
}
