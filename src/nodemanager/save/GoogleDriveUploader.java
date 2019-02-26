package nodemanager.save;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Used to upload files to the google drive
 * @author Matt Crow
 */
public class GoogleDriveUploader {
    private static final String FOLDER_ID = "1-HZrReHNM6szXfmZ1rNoG2HXf2ejal1o"; //the 'Matt, Implement These' folder
    
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
     * Uploads a file to the google drive
     * @param orig the local file to upload
     * @param type the type of the file (text/csv, image/png)
     * @param suppressMessages whether or not to notify the user that files are being uploaded
     * @return the file after it has been uploaded to the google drive
     */
    public static File uploadFile(java.io.File orig, String type, boolean suppressMessages){
        File googleFile = null;
        
        try {
            googleFile = new File();
            FileContent content = new FileContent(type, orig);
            
            
            ArrayList<String> parents = new ArrayList<>();
            parents.add(getTodaysFolder().getId());
            googleFile.setParents(parents);
            
            
            
            Drive.Files.Create insert = drive.files().create(googleFile, content);
            MediaHttpUploader uploader = insert.getMediaHttpUploader();
            
            if(!suppressMessages){
                uploader.setProgressListener((up) -> {
                    switch(up.getUploadState()){
                        case INITIATION_STARTED:
                            JOptionPane.showMessageDialog(null, "Beginning upload to the google drive...");
                            break;
                        case MEDIA_COMPLETE:
                            JOptionPane.showMessageDialog(null, "Upload successful!");
                            break;
                        default:
                            System.out.println(orig.getName() + ": " + up.getUploadState());
                            break;
                    }    
                });
            }
            googleFile.setName(orig.getName());
            
            googleFile = insert.execute();
            publishToWeb(googleFile);
            
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "If you received a 403 error, it could mean you tried to upload using your personal GMail account."
                + " If you want to log in using your student email, delete the file " + STORE.getDataDirectory().getAbsolutePath()
            );
        }
        return googleFile;
    }
    
    public static File uploadFile(java.io.File orig, String type){
        return uploadFile(orig, type, false);
    }
    
    public static File uploadCsv(java.io.File file, boolean suppressMessages){
        return uploadFile(file, "text/csv", suppressMessages);
    }
    
    public static File uploadCsv(java.io.File file){
        return uploadCsv(file, false);
    }
    
    private static File getTodaysFolder(){
        File folder = null;
        try {
            String time = new SimpleDateFormat("MM_dd_yyyy").format(Calendar.getInstance().getTime());
            
            drive.files().list().setQ("parents in '" + FOLDER_ID + "' and trashed = false").execute().getFiles().forEach((file) -> {
                System.out.println(file.getName());
            });
            
            List<File> folders = drive.files().list().setQ("parents in '" + FOLDER_ID + "' and trashed = false and name='" + time + "'").execute().getFiles();
            folders.forEach(n -> System.out.println(n));
            
            if(folders.isEmpty()){
                folders.add(createFolder(time));
                
            }
            folder = drive.files().get(folders.stream().findFirst().get().getId()).execute();
            System.out.println(folder);
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
        getTodaysFolder();
    }
}
