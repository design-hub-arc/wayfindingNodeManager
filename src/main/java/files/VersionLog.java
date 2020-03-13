package files;

import com.google.api.services.drive.model.File;
import static io.StreamReaderUtil.NEWLINE;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import nodemanager.gui.FileSelector;
import nodemanager.io.DriveIOOp;
import nodemanager.io.GoogleDriveUploader;

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
                .download(ID)
                .addOnSucceed((stream)->{
                    setContents(stream);
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
     * @return the updated contents of the version log.
     */
    @Override
    public String getContentsToWrite() {
        StringBuilder sb = new StringBuilder();
        
        ArrayList<String> versions = new ArrayList<>(urls.keySet());
        
        sb.append(String.join(", ", versions));
        
        int maxUrls = 0; //maximum URLs any one version has
        for(ArrayList<String> al : urls.values()){
            if(al.size() > maxUrls){
                maxUrls = al.size();
            }
        }
        
        String[] newRow;
        ArrayList<String> vUrls; //version's URLs
        for(int i = 0; i < maxUrls; i++){
            sb.append(NEWLINE);
            newRow = new String[urls.size()]; //number of columns
            for(int j = 0; j < urls.size(); j++){
                vUrls = urls.get(versions.get(j));
                //           prevent out of bounds               blank if that version doesn't have an i'th URL
                newRow[j] = (vUrls.size() > i) ? vUrls.get(i) : "";
            }
            sb.append(String.join(", ", newRow));
        }
        return sb.toString();
    }
    
    @Override
    public void setContents(InputStream s) {
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
        v.download().addOnSucceed((stream)->{
            System.out.println(v.getContentsToWrite());
            //GoogleDriveUploader.revise(v);
        });
    }

    /**
     * 
     * @return whether or not the call to this.download has completed
     */
    public boolean isDownloaded() {
        return downloaded;
    }

    @Override
    public void importData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
