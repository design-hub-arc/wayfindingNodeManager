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
import com.google.api.services.drive.model.Revision;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
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
     * @return the file after it has been uploaded to the google drive
     */
    public static File uploadFile(java.io.File orig){
        File googleFile = null;
        try {
            googleFile = new File();
            FileContent content = new FileContent("text/csv", orig);
            ArrayList<String> parents = new ArrayList<>();
            parents.add(FOLDER_ID);
            googleFile.setParents(parents);
            Drive.Files.Create insert = drive.files().create(googleFile, content);
            MediaHttpUploader uploader = insert.getMediaHttpUploader();
            
            String id = googleFile.getId();
            Revision pubToWeb = new Revision();
            pubToWeb.setPublished(true);
            pubToWeb.setPublishAuto(true);
            
            
            uploader.setProgressListener((up) -> {
                switch(up.getUploadState()){
                    case INITIATION_STARTED:
                        JOptionPane.showMessageDialog(null, "Beginning upload to the google drive...");
                        break;
                    case MEDIA_COMPLETE:
                        JOptionPane.showMessageDialog(null, "Upload successful! drive.google.com/folders/" + FOLDER_ID); //not able to copy-paste
                        //publishToWeb(googleFile);
                        break;
                    default:
                        System.out.println(orig.getName() + ": " + up.getUploadState());
                        break;
                }
                
            });
            googleFile.setName(orig.getName());
            
            return insert.execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return googleFile;
    }
    
    private static void publishToWeb(File f) throws IOException{
        Revision pubToWeb = new Revision();
        pubToWeb.setPublished(true);
        pubToWeb.setPublishAuto(true);
        drive.revisions().update(f.getId(), FOLDER_ID, pubToWeb).execute();
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
