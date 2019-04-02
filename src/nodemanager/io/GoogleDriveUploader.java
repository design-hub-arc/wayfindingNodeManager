package nodemanager.io;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import javax.swing.JOptionPane;
import nodemanager.exceptions.NoPermissionException;
import nodemanager.exceptions.VersionLogAccessException;

/**
 * Used to upload files to the google drive.
 * We will replace this with database stuff once
 * we actually get one.
 * 
 * @author Matt Crow
 */
public class GoogleDriveUploader{
    private static final String FOLDER_ID = "1-HZrReHNM6szXfmZ1rNoG2HXf2ejal1o"; //the 'Matt, Implement These' folder
    
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
     * @return a DriveIOOp containing an inputstream containing the data of the file
     */
    public static DriveIOOp<InputStream> download(String id){
        
        if(id.contains("id=")){
            //need to do this, as anonnymous class requires that id is final
            return download(id.split("id=")[1]);
        }
        //########################### Ends here and recurs if id was invalid
        
        return new DriveIOOp<InputStream>(){
            @Override
            public InputStream perform() throws Exception {
                ID_TO_NAME.put(id, drive.files().get(id).execute().getName());
                return drive.files().get(id).executeMediaAsInputStream();
            }  
        };
    }
    
    /**
     * Asynchronously uploads a file to the google drive
     * @param f the wayfinding file to upload to the drive
     * @param folderName the folder to upload to
     * @return a DriveIOOp. See DriveIOOp for how to use this
     */
    public static DriveIOOp<File> uploadFile(AbstractWayfindingFile f, String folderName){
        DriveIOOp upload = new DriveIOOp<File>(){
            @Override
            public File perform() throws Exception {
                File googleFile = null;
                try{
                    googleFile = new File();
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
                } catch(GoogleJsonResponseException gex){
                    int code = gex.getDetails().getCode();
                    if(code == 403 || code == 404){
                        deleteFileStore();
                        throw new NoPermissionException(FOLDER_ID);
                    } else {
                        throw gex;
                    }
                }
                return googleFile;
            }
        };
        
        return upload;
    }
    
    public static final DriveIOOp<File> revise(VersionLog vl){
        return new DriveIOOp<File>(){
            @Override
            public File perform() throws Exception {
                File ret = null;
                try{
                    ret = drive.files().get(VersionLog.ID).execute();
                    drive.files().update(ret.getId(), new File(), new FileContent("text/csv", vl.createTemp())).execute();
                } catch(IOException e){
                    if(e instanceof GoogleJsonResponseException){
                        int code = ((GoogleJsonResponseException) e).getDetails().getCode();
                        if(code == 403 || code == 404){
                            throw new VersionLogAccessException();
                        }
                    }
                }
                
                return ret;
            }
        };
    }
    
    private static File getFolderByName(String name) throws IOException{
        File folder = null;
        List<File> folders = drive.files().list().setQ("parents in '" + FOLDER_ID + "' and trashed = false and name='" + name + "'").execute().getFiles();

        if(folders.isEmpty()){
            folders.add(createFolder(name));

        }
        folder = drive.files().get(folders.stream().findFirst().get().getId()).execute();
        
        return folder;
    }
    
    private static File createFolder(String title) throws IOException{
        File folder = new File();

        folder.setName(title);
        folder.setMimeType("application/vnd.google-apps.folder");
        ArrayList<String> parents = new ArrayList<>();
        parents.add(FOLDER_ID);
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
    
    
    
    
    
    
    
    public static String getFileName(String id) throws IOException{
        if(id.contains("id=")){
            id = id.split("id=")[1];
        }
        
        if(!ID_TO_NAME.containsKey(id)){
            ID_TO_NAME.put(id, drive.files().get(id).execute().getName());
        }
        
        return ID_TO_NAME.get(id);
    }
    
    /**
     * These two are used whenever uploadFile 
     * detects a 403 or 404 error, allowing the
     * user to sign in using a different google 
     * account.
     */
    private static void deleteFileStore(){
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

    public static void main(String[] args) throws IOException{
        /*
        try{
            drive.files().update("1HdesCi9x5rD5r7qDNHDp_78ZcI547HFl", new File(), new FileContent("text/csv", new java.io.File("C:\\Users\\w1599227\\Desktop\\test.txt.txt"))).execute();
        } catch(com.google.api.client.googleapis.json.GoogleJsonResponseException e){
            System.out.println(e.getDetails().get("code"));
        }*/
        VersionLog v = new VersionLog();
        v.download();
        v.save();
    }
}
