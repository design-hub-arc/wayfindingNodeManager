package nodemanager.io;

import java.io.BufferedReader;
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
 * TODO: make this able to save a local copy?
 */
public class WayfindingManifest extends AbstractCsvFile{
    private final String prefix;
    private final String inDriveFolder;
    private final HashMap<String, String> urls;
    
    public WayfindingManifest(String folderName){
        super(folderName + "Manifest", FileType.MANIFEST);
        prefix = folderName;
        inDriveFolder = folderName;
        urls = new HashMap<>();
    }
    
    /**
     * Asynchronously imports a manifest from the drive into the program,
     * updating the program's data
     * @param id the file id or url of the manifest
     * @return the DriveIIOp downloading the manifest
     */
    public static DriveIOOp<WayfindingManifest> importManifest(String id){
        if(id.contains("id=")){
            return importManifest(id.split("id=")[1]);
        }
        DriveIOOp<WayfindingManifest> ret = new DriveIOOp<WayfindingManifest>(){
            @Override
            public WayfindingManifest perform() throws Exception {
                WayfindingManifest m = new WayfindingManifest("");
                
                //download the file from the drive
                GoogleDriveUploader.download(id).addOnSucceed((stream)->{
                    m.readStream(stream); //populate
                }).getExcecutingThread().join(); //wait until it's done
                return m;
            }
        };
        return ret;
    }
    
    /**
     * Uploads the contents of the program to the drive,
     * then populates this with the urls of those new files.
     * @return the DriveIOOp populating the manifest. The boolean is just a dummy value, it means nothing
     */
    private DriveIOOp<Boolean> populate(){
        DriveIOOp populate = new DriveIOOp<Boolean>(){
            @Override
            public Boolean perform() throws Exception {
                new NodeCoordFile(prefix).upload(inDriveFolder).addOnSucceed((f)->{
                    urls.put("Node coordinates", "https://drive.google.com/uc?export=download&id=" + ((com.google.api.services.drive.model.File)f).getId());
                }).getExcecutingThread().join();
                
                new NodeConnFile(prefix).upload(inDriveFolder).addOnSucceed((f)->{
                    urls.put("Node connections", "https://drive.google.com/uc?export=download&id=" + ((com.google.api.services.drive.model.File)f).getId());
                }).getExcecutingThread().join();
                
                new NodeLabelFile(prefix).upload(inDriveFolder).addOnSucceed((f)->{
                    urls.put("labels", "https://drive.google.com/uc?export=download&id=" + ((com.google.api.services.drive.model.File)f).getId());
                }).getExcecutingThread().join();
                
                new MapFile(prefix).upload(inDriveFolder).addOnSucceed((f)->{
                    urls.put("map image", "https://drive.google.com/uc?export=download&id=" + ((com.google.api.services.drive.model.File)f).getId());
                }).getExcecutingThread().join();
                
                return true;
            }
        };
        return populate;
    }
    
    public final boolean containsUrlFor(FileType fileType) {
        return urls.containsKey(fileType.getTitle());
    }
    
    public final AbstractWayfindingFile getFileFor(FileType fileType){
        AbstractWayfindingFile ret = null;
        
        if(containsUrlFor(fileType)){
            ret = Converter.convert(GoogleDriveUploader.getFile(urls.get(fileType.getTitle())), fileType);
        }
        
        return ret;
    }
    
    
    @Override
    public String getContentsToWrite() {
        try {
            populate().getExcecutingThread().join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
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
