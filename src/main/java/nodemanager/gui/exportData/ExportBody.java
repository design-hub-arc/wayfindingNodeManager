package nodemanager.gui.exportData;

import com.google.api.services.drive.model.File;
import nodemanager.gui.InputConsole;
import nodemanager.files.VersionLog;
import nodemanager.files.WayfindingManifest;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;
import nodemanager.NodeManager;
import nodemanager.exceptions.NoPermissionException;
import nodemanager.exceptions.VersionLogAccessException;
import nodemanager.gui.ApplicationBody;
import nodemanager.io.*;
import nodemanager.gui.ApplicationPage;
import static nodemanager.io.GoogleDriveUploader.DOWNLOAD_URL_PREFIX;

/**
 * Acts as the body of the export dialog whenever the user clicks the export to drive button.
 * Allows the user to specify which version of wayfinding they are uploading to, as well as what to name the export.
 * 
 * @author Matt Crow
 */
public final class ExportBody extends ApplicationPage {
    public static final String NEW_TYPE = "New type";
    private final JTextField name;
    private final JTextField folder;
    private final JComboBox<String> selectType;
    private final JButton exportButton;
    private final JTextArea msg;
    private final VersionLog v;
    private volatile boolean updating;
    
    public ExportBody(ApplicationBody parent) {
        super(parent);
        updating = false;
        
        setLayout(new GridLayout(4, 2));
        name = new JTextField("Enter the name for this export");
        
        folder = new JTextField(GoogleDriveUploader.DEFAULT_FOLDER_ID);
        
        selectType = new JComboBox<>();
        selectType.addItemListener((ItemEvent e)->{
            if(selectType.getSelectedItem().equals(NEW_TYPE) && !updating){
                InputConsole.getInstance().askString(
                    "Enter the name of this new version:", 
                    (String versionName)->{
                        updating = true;
                
                        if(selectType.getItemCount() > 1){
                            selectType.setSelectedIndex(0);
                        }
                        selectType.insertItemAt(versionName, selectType.getItemCount() - 1);
                        selectType.setSelectedItem(versionName);
                        revalidate();
                        repaint();
                        
                        updating = false;
                    }
                );
            }
        });
        
        msg = new JTextArea("please wait while I download the version log...");
        msg.setEditable(false);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(msg);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        exportButton = createExportButton();
        
        add(new JLabel("Name this export"));
        add(name);
        add(new JLabel("Enter folder to upload to"));
        add(folder);
        add(new JLabel("Select version"));
        add(selectType);
        add(scroll);
        add(exportButton);
        
        v = new VersionLog();
        tryDownloadVersionLog();
    }
    
    private void tryDownloadVersionLog(){
        try {
            InputStream stream = GoogleDriveUploader.download(VersionLog.DEFAULT_VERSION_LOG_ID);
            v.readGraphDataFromFile(null, stream);
            for(String option : v.getTypes()){
                selectType.addItem(option);
            }
            selectType.addItem(NEW_TYPE);
            selectType.setSelectedIndex(0);
            msg.setText("Ready to export!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Called by the export button
     */
    private void upload(){
        msg.setText("Beginning upload...");
        revalidate();
        repaint();
        WayfindingManifest newMan = new WayfindingManifest(name.getText());
        try {
            File f = GoogleDriveUploader.uploadManifest(newMan, folder.getText());
            msg.setText("Upload complete!");
            NodeManager.getInstance().getLog().clear();
            v.addExport((String)selectType.getSelectedItem(), DOWNLOAD_URL_PREFIX + f.getId());
            try {
                GoogleDriveUploader.revise(v);
                getApplicationBody().switchToPage(ApplicationBody.EDIT);
            } catch (VersionLogAccessException ex) {
                msg.setText(ex.getMessage());
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            msg.setText(ex.getMessage());
            ex.printStackTrace();
        } catch (NoPermissionException ex) {
            msg.setText(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private JButton createExportButton(){
        JButton ret = new JButton("Export");
        
        ret.addActionListener((ae)->{
            msg.setText("Verifying this upload will work...");
            revalidate();
            repaint();
            
            try {
                // Second, make sure the user is trying to upload to a folder
                boolean isFolder = GoogleDriveUploader.isFolder(folder.getText());
                if(isFolder){
                    msg.setText("Looks like that's a folder! uploading...");
                    upload(); //uploads here
                } else {
                    msg.setText("Nope, not a folder.");
                }
            } catch (IOException ex) {
                msg.setText("Hmm... looks like that file doesn't exist. Could you double check to make sure you have access?");
            }
        });
        
        return ret;
    }
}
