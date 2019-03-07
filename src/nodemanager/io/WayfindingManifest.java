package nodemanager.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.out;
import java.util.Arrays;
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
public class WayfindingManifest extends AbstractCsvFile{
    private final String prefix;
    private final String inDriveFolder;
    private final HashMap<String, String> urls;
    
    public WayfindingManifest(String folderName){
        super(folderName + "Manifest");
        prefix = folderName;
        inDriveFolder = folderName;
        urls = new HashMap<>();
    }
    
    /**
     * Imports a manifest from the drive into the program,
     * updating the program's data
     * @param id the file id or url of the manifest
     */
    public static void importManifest(String id){
        WayfindingManifest m = new WayfindingManifest("");
        if(id.contains("id=")){
            id = id.split("id=")[1];
        }
        m.readStream(GoogleDriveUploader.download(id));
        m.unpack();
    }
    
    /**
     * Uploads the contents of the program to the drive,
     * then populates this with the urls of those new files.
     */
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
        
        try{
            googleFile = new MapFile(prefix).upload(inDriveFolder);
            urls.put("map image", "https://drive.google.com/uc?export=download&id=" + googleFile.getId());
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
   
    /**
     * Loads the data from this manifest into the program
     */
    private final void unpack(){
        out.println("Unpacking...");
        
        if(urls.containsKey("map image")){
            new MapFile("").readStream(GoogleDriveUploader.download(urls.get("map image")));
        } else {
            System.err.println("Manifest missing 'map image'");
        }
        
        if(urls.containsKey("Node coordinates")){
            new NodeCoordFile("").readStream(GoogleDriveUploader.download(urls.get("Node coordinates")));
        } else {
            System.err.println("Manifest missing 'Node coordinates'");
        }
        
        if(urls.containsKey("Node connections")){
            new NodeConnFile("").readStream(GoogleDriveUploader.download(urls.get("Node connections")));
        } else {
            System.err.println("Manifest missing 'Node connections'");
        }
        
        if(urls.containsKey("labels")){
            new NodeLabelFile("").readStream(GoogleDriveUploader.download(urls.get("labels")));
        } else {
            System.err.println("Manifest missing 'labels'");
        }
        out.println("done");
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
        BufferedReader br;
        String[] line = new String[0];
        boolean firstLine = true;
        try{
            br = new BufferedReader(new InputStreamReader(s));
            while(br.ready()){
                try{
                    line = br.readLine().split(",");
                    if(!firstLine){
                        urls.put(line[0], line[1]);
                    }
                } catch(Exception e){
                    if(!firstLine){
                        //don't print errors for first line, as it will always fail, being a header
                        out.println("Line fail: " + Arrays.toString(line));
                        e.printStackTrace();
                    }
                }
                firstLine = false;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
