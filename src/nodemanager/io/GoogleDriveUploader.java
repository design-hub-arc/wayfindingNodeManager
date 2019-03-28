package nodemanager.io;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploader.UploadState;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.out;
import java.util.*;
import java.util.function.Consumer;
import javax.swing.JOptionPane;

/**
 * Used to upload files to the google drive.
 * We will replace this with database stuff once
 * we actually get one.
 * 
 * @author Matt Crow
 */
public class GoogleDriveUploader{
    private static final String FOLDER_ID = "1-HZrReHNM6szXfmZ1rNoG2HXf2ejal1o"; //the 'Matt, Implement These' folder
    
    private static HashMap<String, String> idToName = new HashMap<>();
    
    private static JacksonFactory JSON;
    private static HttpTransport HTTP;
    private static FileDataStoreFactory STORE;
    private static Drive drive; //note that this is not a given drive, it is the drive service provider
    
    
    static{
        JSON = JacksonFactory.getDefaultInstance();
        try {
            HTTP = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
        try {
            STORE = new FileDataStoreFactory(
                    new java.io.File(
                            System.getProperty("user.home"), 
                            ".store/wayfindingNodeManager"
                    )
            );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        try {
            drive = new Drive.Builder(HTTP, JSON, authorize()).build();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    /**
     * Asynchronously uploads a file to the google drive
     * @param f the wayfinding file to upload to the drive
     * @param folderName the folder to upload to
     * @param onUploadComplete a method that is called once the new file is uploaded, accepting the newly uploaded drive file as a parameter
     */
    public static void uploadFile(AbstractWayfindingFile f, String folderName, Consumer<File> onUploadComplete){
        new Thread(){
            @Override
            public void run(){
                try{
                    File googleFile = new File();
                    FileContent content = new FileContent(f.getType().getMimeType(), f.getUpload());

                    ArrayList<String> parents = new ArrayList<>();
                    parents.add(getFolderByName(folderName).getId());
                    googleFile.setParents(parents);

                    Drive.Files.Create insert = drive.files().create(googleFile, content);

                    googleFile.setName(f.getUpload().getName());

                    //since all of this google stuff is blocking code (O...K...)
                    //it will execute in whatever order we put it in.
                    //no need to worry about them executing out of order
                    googleFile = insert.execute();
                    publishToWeb(googleFile);
                    onUploadComplete.accept(googleFile);
                } catch(IOException e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "If you received a 403 error, it could mean you tried to upload using your personal GMail account. Please log in using your student email.");
                    try {
                        java.nio.file.Files.deleteIfExists(STORE.getDataDirectory().toPath());
                    } catch (IOException ex1) {
                        ex1.printStackTrace();
                    }
                }
            }
        }.start();
    }
    
    public static final com.google.api.services.drive.model.File revise(VersionLog vl) throws IOException{
        com.google.api.services.drive.model.File file = drive.files().get(VersionLog.ID).execute();
        drive.files().update(file.getId(), new File(), new FileContent("text/csv", vl.createTemp())).execute();
        return file;
    }
    
    private static File getFolderByName(String name){
        File folder = null;
        try {
            
            List<File> folders = drive.files().list().setQ("parents in '" + FOLDER_ID + "' and trashed = false and name='" + name + "'").execute().getFiles();
            folders.forEach(n -> System.out.println(n));
            
            if(folders.isEmpty()){
                folders.add(createFolder(name));
                
            }
            folder = drive.files().get(folders.stream().findFirst().get().getId()).execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return folder;
    }
    
    private static File createFolder(String title){
        File folder = null;
        try {
            folder = new File();
            
            folder.setName(title);
            folder.setMimeType("application/vnd.google-apps.folder");
            ArrayList<String> parents = new ArrayList<>();
            parents.add(FOLDER_ID);
            folder.setParents(parents);
            
            folder = drive.files().create(folder).setFields("id").execute();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return folder;
    }
    
    private static void publishToWeb(File f) throws IOException{
        Permission p = new Permission();
        p.setType("anyone");
        p.setAllowFileDiscovery(true);
        p.setRole("reader");
        drive.permissions().create(f.getId(), p).execute();
    }
    
    public static com.google.api.services.drive.model.File getFile(String id){
        com.google.api.services.drive.model.File ret = null;
        if(id.contains("id=")){
            id = id.split("id=")[1];
        }
        try{
            ret = drive.files().get(id).execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return ret;
    }
    
    /**
     * Gets the contents of a file in the Google Drive with the given ID
     * @param id either the id of a file, or a url to that file
     * @return and inputstream containing the data of the file, or null if it wasn't found
     */
    public static InputStream download(String id){
        InputStream ret = null;
        
        if(id.contains("id=")){
            id = id.split("id=")[1];
        }
        
        try{
            ret = drive.files().get(id).executeMediaAsInputStream();
        } catch(IOException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Couldn't download " + id, "Faied to download", JOptionPane.ERROR_MESSAGE);
        }
        return ret;
    }
    
    
    public static String getFileName(String id) throws IOException{
        if(id.contains("id=")){
            id = id.split("id=")[1];
        }
        
        if(!idToName.containsKey(id)){
            idToName.put(id, drive.files().get(id).execute().getName());
        }
        
        return idToName.get(id);
    }
    
    private static Credential authorize() throws Exception{
        //load a json file containing the user's login data
        GoogleClientSecrets clientInfo = GoogleClientSecrets.load(
                JSON, 
                new InputStreamReader(
                        GoogleDriveUploader
                                .class
                                .getResourceAsStream("/client_id.json")
                )
        );
        if(
            clientInfo.getDetails().getClientId().startsWith("NULL") ||
            clientInfo.getDetails().getClientSecret().startsWith("NULL")
        ){
            System.out.println("Enter client ID and secret from https://code.google.com/apis/console/?api=drive into the client_id file");
            System.exit(1);
        }
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP, JSON, clientInfo, Collections.singleton(DriveScopes.DRIVE))
                .setDataStoreFactory(STORE).build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
}
