package nodemanager.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

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
public class VersionLog extends AbstractWayfindingFile{
    public static final String ID = "1Q99ku0cMctu3kTN9OerjFsM9Aj-nW6H5";
    public static final String URL = "https://drive.google.com/open?id=" + ID;
    private final LinkedHashMap<String, ArrayList<String>> urls; //Each column is a wayfinding type(artfinding, wayfinding, etc), and each cell in the column is the URL of a manifest
    
    public VersionLog(){
        super("versions", FileType.CSV);
        urls = new LinkedHashMap<>();
    }
    
    /**
     * Downloads versions.csv, then populates urls to match its data
     */
    public void download(){
        InputStream is = GoogleDriveUploader.download(URL);
        if(is != null){
            readStream(is);
        }
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
    
    public void addType(String type){
        urls.put(type, new ArrayList<>());
    }
    
    public String[] getTypes(){
        return Arrays.copyOf(urls.keySet().toArray(), urls.size(), String[].class);
    }
    
    public String[] getExportsFor(String type){
        return Arrays.copyOf(urls.get(type).toArray(), urls.get(type).size(), String[].class);
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
    
    public void save() throws IOException{
        GoogleDriveUploader.revise(this);
    }
    
    public void displayData(){
        System.out.println(getContentsToWrite());
    }
    
    public static void main(String[] args) throws IOException{
        VersionLog v = new VersionLog();
        v.download();
        //GoogleDriveUploader.revise(v);
    }
}
