package files;

import static io.StreamReaderUtil.NEWLINE;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import nodemanager.io.GoogleDriveUploader;

/**
 * Used to interact with the versions.csv file in the google drive.
 * 
 * Since Wayfinding works by querying a manifest, we run into a problem when we create a new manifest:
 * We need a way to keep track of previous versions, so if something goes wrong, we can just revert the update,
 * but we also need to be able to push changes from this manager to Wayfinding without manually changing which manifest it queries.
 * Versions.csv contains a list of manifests, which the program queries, and will run through the manifests listed until it finds one that works.
 * 
 * Note that the VersionLog must be saved for changes to take effect.
 * (more on this later)
 * 
 * @author Matt Crow
 */
public class VersionLog extends AbstractCsvFile{
    public static final String DEFAULT_VERSION_LOG_ID = "1Q99ku0cMctu3kTN9OerjFsM9Aj-nW6H5";
    
    /**
     * Key is the wayfinding type (wayfinding, artfinding, etc)
     * Value is the list of URLs for exports for that version,
     * ordered oldest to newest
     */
    private final HashMap<String, ArrayList<String>> exports;
    
    public VersionLog(){
        super("versions", FileType.VERSION_LOG);
        exports = new HashMap<>();
    }
    
    
    /*
    Export editting methods
    */
    
    
    /**
     * Adds an export URL to the VersionLog.
     * 
     * @param wayfindingType the type of wayfinding this export is for.
     * If this type is not listed in this Version Log, it will create a new 
     * type.
     * 
     * @param url the URL of the WayfindingManifest on Google Drive to add
     * as an export for the given wayfinding type.
     * 
     * @return this, for chaining purposes. 
     */
    public final VersionLog addExport(String wayfindingType, String url){
        if(!exports.containsKey(wayfindingType)){
            exports.put(wayfindingType, new ArrayList<>());
        }
        exports.get(wayfindingType).add(url);
        
        return this;
    }
    
    /**
     * Removes the export with the given type and url from the VersionLog, if such an export exists.
     * Note that this does not delete anything from the Google Drive.
     * 
     * @param wayfindingType the type of wayfinding this export is for.
     * If this type is not listed in this Version Log, it will create a new 
     * type.
     * 
     * @param url the URL of the WayfindingManifest on Google Drive to add
     * as an export for the given wayfinding type.
     * 
     * @return whether or not the given export exists, and was therefore removed 
     */
    public final boolean removeExport(String wayfindingType, String url){
        boolean wasRemoved = false;
        
        if(exports.containsKey(wayfindingType) && exports.get(wayfindingType).contains(url)){
            exports.get(wayfindingType).remove(url);
            wasRemoved = true;
        }
        
        return wasRemoved;
    }
    
    /**
     * Removes all exports with the given type from the
     * VersionLog, and deletes that column.
     * 
     * @param wayfindingType the type to remove.
     * 
     * @return if the given type existed in the version log, and therefore was removed. 
     */
    public final boolean removeType(String wayfindingType){
        boolean wasRemoved = false;
        if(exports.containsKey(wayfindingType)){
            exports.remove(wayfindingType);
            wasRemoved = true;
        }
        return wasRemoved;
    }
    
    
    /*
    Getters
    */
    
    
    /**
     * 
     * @return the different wayfinding types listed in this VersionLog 
     */
    public String[] getTypes(){
        return exports.keySet().toArray(new String[exports.size()]);
    }
    
    /**
     * 
     * @param wayfindingType the wayfindingType to get
     * exports for.
     * 
     * @return an array of the URLs of exports for the given type,
     * ordered oldest to newest. If the given type is not listed in
     * this VersionLog, returns an empty array.
     */
    public String[] getExportsFor(String wayfindingType){
        String[] exportUrls = new String[0];
        if(exports.containsKey(wayfindingType)){
            exportUrls = exports.get(wayfindingType).toArray(new String[exports.get(wayfindingType).size()]);
        }
        return exportUrls;
    }
    
    
    
    
    
    
    
    /**
     * Gets what to write to the version log.
     * @return the updated contents of the version log.
     */
    @Override
    public String getContentsToWrite() {
        StringBuilder sb = new StringBuilder();
        
        ArrayList<String> versions = new ArrayList<>(exports.keySet());
        
        sb.append(String.join(", ", versions));
        
        int maxUrls = 0; //maximum URLs any one version has
        for(ArrayList<String> al : exports.values()){
            if(al.size() > maxUrls){
                maxUrls = al.size();
            }
        }
        
        String[] newRow;
        ArrayList<String> vUrls; //version's URLs
        for(int i = 0; i < maxUrls; i++){
            sb.append(NEWLINE);
            newRow = new String[exports.size()]; //number of columns
            for(int j = 0; j < exports.size(); j++){
                vUrls = exports.get(versions.get(j));
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
                        exports.put(header, new ArrayList<>());
                    }
                } else {
                    row = br.readLine().split(",");
                    for(int i = 0; i < row.length; i++){
                        if(!row[i].equals("")){
                            addExport(headers[i], row[i]);
                        }
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    public static void main(String[] args) throws IOException{
        VersionLog v = new VersionLog();
        GoogleDriveUploader.download(DEFAULT_VERSION_LOG_ID).addOnSucceed((stream)->{
            v.setContents(stream);
            System.out.println(v.getContentsToWrite());
            //GoogleDriveUploader.revise(v);
        });
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
