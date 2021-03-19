package nodemanager.io;

import nodemanager.files.VersionLog;
import nodemanager.files.AbstractWayfindingFileHelper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import nodemanager.files.WayfindingManifest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import nodemanager.NodeManager;
import nodemanager.exceptions.NoPermissionException;
import nodemanager.exceptions.VersionLogAccessException;
import nodemanager.model.Graph;

/**
 * Used to upload files to the google drive.
 * We will replace this with database stuff once
 * we actually get one.
 * 
 * @author Matt Crow
 */
public class GoogleDriveUploader{
    public static final String DOWNLOAD_URL_PREFIX = "https://drive.google.com/uc?export=download&id=";
    public static final String DEFAULT_FOLDER_ID = "1-HZrReHNM6szXfmZ1rNoG2HXf2ejal1o"; //the 'Matt, Implement These' folder
    
    //used to make it easier to reference files
    private static final HashMap<String, String> ID_TO_NAME = new HashMap<>();
    
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
     * Gets the contents of a file in the Google Drive with the given ID
     * @param id either the id of a file, or a url to that file
     * @return an inputstream containing the data of the file
     */
    public static final InputStream download(String id) throws IOException{
        if(id.contains("id=")){
            id = id.split("id=")[1];
        }
        //ID_TO_NAME.put(id, drive.files().get(id).execute().getName());
        return drive.files().get(id).executeMediaAsInputStream();
    }
    
    /**
     * Synchronously uploads a file to the google drive
     * @param f the wayfinding file to upload to the drive
     * @param folderId the id of the folder to upload to
     * @return the newly uploaded file
     */
    public static File uploadFile(Graph g, AbstractWayfindingFileHelper f, String folderId) throws IOException, NoPermissionException{
        File googleFile = null;
        try{
            java.io.File localFileToUpload = f.writeToTempFile(g);

            googleFile = new File();
            FileContent content = new FileContent(f.getType().getMimeType(), localFileToUpload);

            ArrayList<String> parents = new ArrayList<>();
            parents.add(folderId);
            googleFile.setParents(parents);

            Drive.Files.Create insert = drive.files().create(googleFile, content);

            googleFile.setName(f.getFileName());

            //since all of this google stuff is blocking code (O...K...)
            //it will execute in whatever order we put it in.
            //no need to worry about them executing out of order
            googleFile = insert.execute();
            publishToWeb(googleFile);
        } catch(GoogleJsonResponseException gex){
            int code = gex.getDetails().getCode();
            if(code == 403 || code == 404){
                deleteFileStore();
                throw new NoPermissionException(DEFAULT_FOLDER_ID);
            } else {
                throw gex;
            }
        }
        return googleFile;
    }
    
    public static final File uploadManifest(WayfindingManifest man, String folderId) throws IOException, NoPermissionException{
        Graph g = NodeManager.getInstance().getGraph();
        
        File newFolder = createSubfolder(folderId, man.getTitle());
        man.setDriveFolderId(newFolder.getId());
        
        man.uploadContents(g);
        
        return uploadFile(g, man, man.getDriveFolderId());
    }
    
    public static final File revise(VersionLog vl) throws IOException, VersionLogAccessException{
        File ret = null;
        try{
            ret = drive.files().get(VersionLog.DEFAULT_VERSION_LOG_ID).execute();
            java.io.File localFileToUpdate = vl.writeToTempFile(null);
            drive.files().update(ret.getId(), new File(), new FileContent("text/csv", localFileToUpdate)).execute();
        } catch(IOException e){
            if(e instanceof GoogleJsonResponseException){
                int code = ((GoogleJsonResponseException) e).getDetails().getCode();
                if(code == 403 || code == 404){
                    throw new VersionLogAccessException();
                } else {
                    throw e;
                }
            }
        }

        return ret;
    }
    
    /**
     * Creates a new folder on the google drive
     * @param id the id of the folder containing this new folder
     * @param name the name of this new folder
     * @return the folder
     */
    public static final File createSubfolder(String id, String name) throws IOException{
        File folder = new File();
        folder.setName(name);
        folder.setMimeType("application/vnd.google-apps.folder");
        ArrayList<String> parents = new ArrayList<>();
        parents.add(id);
        folder.setParents(parents);
        folder = drive.files().create(folder).setFields("id").execute();
        return folder;
    }
    
    private static void publishToWeb(File f) throws IOException{
        Permission p = new Permission();
        p.setType("anyone");
        p.setAllowFileDiscovery(true);
        p.setRole("reader");
        drive.permissions().create(f.getId(), p).execute();
    }
    
    public static final com.google.api.services.drive.model.File getFile(String id) throws IOException{
        com.google.api.services.drive.model.File ret = null;
        if(id.contains("id=")){
            id = id.split("id=")[1];
        }
        ret = drive.files().get(id).execute();
        
        return ret;
    }
    
    /**
     * Checks if a file is a folder
     * @param id the id of the file to check
     * @return a whether or not the file is a folder
     * @throws java.io.IOException of this fails to retrieve the folder
     */
    public static final boolean isFolder(String id) throws IOException{
        if(id.contains("id=")){
            id = id.split("id=")[1];
        }
        
        File f = getFile(id);
        ID_TO_NAME.put(id, f.getName());
        return f.getMimeType().equals("application/vnd.google-apps.folder");
    }
    
    /**
     * Synchronously retrieves the name of the Google Drive file with the given
     * ID. Note: this method is blocking.
     * 
     * @param id
     * @return 
     * @throws java.io.IOException if this fails to retrieve the file
     */
    public static final String getFileName(String id) throws IOException{
        String fileName;
        
        if(id.contains("id=")){
            id = id.split("id=")[1];
        }
        if(ID_TO_NAME.containsKey(id)){
            fileName = ID_TO_NAME.get(id);
        } else {
            fileName = getFile(id).getName();
            ID_TO_NAME.put(id, fileName);
        }
        
        return fileName;
    }
    
    /**
     * These two are used whenever uploadFile 
     * detects a 403 or 404 error, allowing the
     * user to sign in using a different google 
     * account.
     */
    public static void deleteFileStore(){
        deleteDir(STORE.getDataDirectory());
    }
    private static void deleteDir(java.io.File f){
        java.io.File[] contents = f.listFiles();
        if(contents != null){
            for(java.io.File file : contents){
                file.delete();
            }
        }
        f.delete();
    }
    
    private static Credential authorize() throws Exception{
        //load a json file containing the user's login data
        GoogleClientSecrets clientInfo = GoogleClientSecrets.load(
            JSON, 
            new InputStreamReader(
                GoogleDriveUploader.class.getResourceAsStream("/client_id.json")
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
