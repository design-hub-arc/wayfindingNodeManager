package nodemanager.io;

import com.google.api.services.drive.model.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import nodemanager.exceptions.NoPermissionException;

/**
 * Used to interact with the versions.csv file in the google drive.
 * 
 * Since Wayfinding works by querying a manifest, we run into a problem when we create a new manifest:
 * We need a way to keep track of previous versions, so if something goes wrong, we can just revert the update,
 * but we also need to be able to push changes from this manager to Wayfinding without manually changing which manifest it queries.
 * Versions.csv contains a list of manifests, which the program queries, and will run through the manifests listed until it finds one that works.
 * 
 * @author Matt Crow
 */
public class VersionLog extends AbstractCsvFile{
    public static final String ID = "1Q99ku0cMctu3kTN9OerjFsM9Aj-nW6H5";
    public static final String URL = "https://drive.google.com/open?id=" + ID;
    private boolean downloaded;
    private final LinkedHashMap<String, ArrayList<String>> urls; //Each column is a wayfinding type(artfinding, wayfinding, etc), and each cell in the column is the URL of a manifest
    
    public VersionLog(){
        super("versions", FileType.VERSION_LOG);
        urls = new LinkedHashMap<>();
        downloaded = false;
    }
    
    /**
     * Downloads versions.csv, then populates urls to match its data
     * @return the DriveIOOp downloading this
     */
    public DriveIOOp<InputStream> download(){
        return GoogleDriveUploader
                .download(URL)
                .addOnSucceed((stream)->{
                    readStream(stream);
                    downloaded = true;
                });
    }
    
    /**
     * Adds a url to the version log. Note that this does not edit the file
     * @param wayfindingVersion the header of the column you want to add the url to
     * @param url the url to the manifest file you want to add to the version log
     */
    public void addUrl(String wayfindingVersion, String url){
        if(!urls.containsKey(wayfindingVersion)){
            urls.put(wayfindingVersion, new ArrayList<>());
        } //note that this is not an if/else
        urls.get(wayfindingVersion).add(url);
    }
    
    public boolean deleteUrl(String url){
        boolean found = false;
        
        return found;
    }
    
    public void addType(String type){
        urls.put(type, new ArrayList<>());
    }
    
    public void deleteType(String type){
        urls.remove(type);
    }
    
    public String[] getTypes(){
        return Arrays.copyOf(urls.keySet().toArray(), urls.size(), String[].class);
    }
    
    public String[] getExportIdsFor(String type){
        ArrayList<String> exportIds = new ArrayList<>();
        ArrayList<String> uploads = urls.get(type);
        for(int i = 0; i < uploads.size(); i++){
            try{
                GoogleDriveUploader.getFileName(uploads.get(i)); //checks if file exists, else throws error
                exportIds.add(uploads.get(i));
            } catch(IOException e){
                System.err.println("Not file id: " + uploads.get(i));
            }
        }
        return Arrays.copyOf(exportIds.toArray(), exportIds.size(), String[].class);
    }
    
    public String[] getExportNamesFor(String type){
        ArrayList<String> exportNames = new ArrayList<>();
        ArrayList<String> uploads = urls.get(type);
        for(int i = 0; i < uploads.size(); i++){
            try{
                exportNames.add(GoogleDriveUploader.getFileName(uploads.get(i)));
            } catch(IOException e){
                System.err.println("Couldn't get the name of " + uploads.get(i));
            }
        }
        return Arrays.copyOf(exportNames.toArray(), exportNames.size(), String[].class);
    }
    
    /**
     * Gets what to write to the version log.
     * Currently not optimal, so I'll probably redo it later.
     * @return the updated contents of the version log.
     */
    @Override
    public String getContentsToWrite() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true; //first cell in row
        
        for(String header : urls.keySet()){
            if(isFirst){
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append(header);
        }
        
        boolean good = urls.values().stream().anyMatch((list)->!list.isEmpty()); //good if any have any elements
        int idx = 0;
        while(good){
            sb.append(NL);
            isFirst = true;
            for(ArrayList list : urls.values()){
                if(isFirst){
                    isFirst = false;
                } else {
                    sb.append(",");
                }
                sb.append((list.size() > idx) ? list.get(idx) : "");
            }
            
            //check if good. Check if any list has urls left to append to the stream
            /*
            lambda doesn't work because of .size()
            good = urls.values().stream().anyMatch((list)->{
                return list.size() > idx;
            });*/
            
            good = false;
            idx++;
            for(ArrayList<String> urlList : urls.values()){
                if(urlList.size() > idx){
                    good = true;
                }
            }
        }
        
        return sb.toString();
    }
    
    @Override
    public void readStream(InputStream s) {
        BufferedReader br = new BufferedReader(new InputStreamReader(s));
        boolean isFirstLine = true;
        String[] headers = {};
        String[] row;
        
        try{
            while(br.ready()){
                if(isFirstLine){
                    isFirstLine = false;
                    headers = br.readLine().split(",");
                    for(String header : headers){
                        urls.put(header, new ArrayList<>());
                    }
                } else {
                    row = br.readLine().split(",");
                    for(int i = 0; i < row.length; i++){
                        if(!row[i].equals("")){
                            addUrl(headers[i], row[i]);
                        }
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public DriveIOOp<File> save(){
        return GoogleDriveUploader.revise(this);
    }
    
    public void displayData(){
        System.out.println(getContentsToWrite());
    }
    
    public static void main(String[] args) throws IOException{
        VersionLog v = new VersionLog();
        v.download();
        //GoogleDriveUploader.revise(v);
    }

    /**
     * 
     * @return whether or not the call to this.download has completed
     */
    public boolean isDownloaded() {
        return downloaded;
    }
}
