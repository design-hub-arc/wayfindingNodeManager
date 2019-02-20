package nodemanager.gui;

import javax.swing.JMenuItem;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.services.drive.Drive;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.drive.model.File;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nodemanager.FileUploadProgressListener;

/**
 *
 * @author Matt Crow
 */
public class ExportToDriveButton extends JMenuItem{
    private Drive drive;
    private static final JacksonFactory JSON = JacksonFactory.getDefaultInstance();
    private static FileDataStoreFactory STORE = null;
    private static HttpTransport HTTP = null;
    private static final String FOLDER_ID = "1-HZrReHNM6szXfmZ1rNoG2HXf2ejal1o";
    static {
        try {
            HTTP = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException ex) {
            
        } catch (IOException ex) {
            
        }
        
        try {
            STORE = new FileDataStoreFactory(new java.io.File(System.getProperty("user.home"), ".store/wayfindingNodeManager"));
        } catch (IOException ex) {
        }
    }
    
    public ExportToDriveButton(){
        super("Export to Drive");
        initDrive();
    }
    
    private void initDrive(){
        try {
            drive = new Drive.Builder(HTTP, JSON, authorize()).build();
        } catch (GeneralSecurityException ex) {
            
        } catch (IOException ex) {
            
        } catch (Exception ex) {
            
        }
    }
    
    private Credential authorize() throws Exception{
        //load a json file containing the user's login data
        GoogleClientSecrets clientInfo = GoogleClientSecrets.load(JSON, new InputStreamReader(this.getClass().getResourceAsStream("/client_id.json")));
        if(
            clientInfo.getDetails().getClientId().startsWith("NULL") ||
            clientInfo.getDetails().getClientSecret().startsWith("NULL")
        ){
            System.out.println("Enter client ID and secret from https://code.google.com/apis/console/?api=drive into the clientData file");
            System.exit(1);
        }
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP, JSON, clientInfo, Collections.singleton(DriveScopes.DRIVE))
                .setDataStoreFactory(STORE).build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
    
    public File uploadFile(java.io.File orig){
        try {
            File googleFile = new File();
            FileContent content = new FileContent("text/csv", orig);
            System.out.println(drive.getRootUrl());
            System.out.println(drive.getBaseUrl());
            System.out.println(drive.files().list().toString());
            ArrayList<String> parents = new ArrayList<>();
            parents.add(FOLDER_ID);
            googleFile.setParents(parents);
            Drive.Files.Create insert = drive.files().create(googleFile, content);
            MediaHttpUploader uploader = insert.getMediaHttpUploader();
            uploader.setProgressListener(new FileUploadProgressListener());
            googleFile.setName(orig.getName());
            
            return insert.execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
