package nodemanager.gui.exportData;

import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;
import nodemanager.Session;
import nodemanager.io.*;

/**
 * Acts as the body of the export dialog whenever the user clicks the export to drive button.
 * Allows the user to specify which version of wayfinding they are uploading to, as well as what to name the export.
 * 
 * @author Matt Crow
 */
public final class ExportBody extends Container {
    public static final String NEW_TYPE = "New type";
    private final JTextField name;
    private final JTextField folder;
    private final JComboBox<String> selectType;
    private final JButton exportButton;
    private final JTextArea msg;
    private final VersionLog v;
    private volatile boolean updating;
    
    public ExportBody() {
        super();
        updating = false;
        
        setLayout(new GridLayout(4, 2));
        name = new JTextField("Enter the name for this export");
        
        folder = new JTextField(GoogleDriveUploader.DEFAULT_FOLDER_ID);
        
        selectType = new JComboBox<>();
        selectType.addItemListener((ItemEvent e)->{
            if(selectType.getSelectedItem().equals(NEW_TYPE) && !updating){
                String versionName = JOptionPane.showInputDialog("Enter the name of this new version:");
                
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
        downloadVersionLog();
    }
    
    private void downloadVersionLog(){
        v.download().addOnSucceed((stream)->{
            for(String option : v.getTypes()){
                selectType.addItem(option);
            }
            selectType.addItem(NEW_TYPE);
            selectType.setSelectedIndex(0);
            msg.setText("Ready to export!");
        });
    }
    
    /**
     * Called by the export button
     */
    private void upload(){
        msg.setText("Beginning upload...");
        revalidate();
        repaint();
        WayfindingManifest newMan = new WayfindingManifest(name.getText());
        newMan.upload(folder.getText()).addOnSucceed((f)->{
            msg.setText("Upload complete!");
            Session.purgeActions();
            v.addUrl((String)selectType.getSelectedItem(), newMan.getUrl());
            v.save().addOnFail((err)->{
                msg.setText(err.getMessage());
            });
        }).addOnFail((ex)->msg.setText(ex.getMessage()));
    }
    
    private JButton createExportButton(){
        JButton ret = new JButton("Export");
        
        ret.addActionListener((ae)->{
            msg.setText("Verifying this upload will work...");
            revalidate();
            repaint();
            
            
            // First, makes sure the Drive API is available
            try{
                Class.forName("com.google.api.client.http.HttpTransport"); 
                //will throw an error if don't have google drive API
            } catch(ClassNotFoundException ex){
                ex.printStackTrace();
                msg.setText(
                        "Looks like you forgot to include the lib folder \n"
                      + "1. Download lib.zip from the node manager Google drive \n"
                      + "2. Extract the contents so that the lib folder is in the same folder as NodeManager.jar \n"
                      + "3. Click export again"
                );
                return;
            }
            // Second, make sure the user is trying to upload to a folder
            GoogleDriveUploader.isFolder(folder.getText())
                .addOnFail((ex)->{
                    msg.setText("Hmm... looks like that file doesn't exist. Could you double check to make sure you have access?");
                })
                .addOnSucceed((bool)->{
                    if(bool){
                        msg.setText("Looks like that's a folder! uploading...");
                        upload(); //uploads here
                    } else {
                        msg.setText("Nope, not a folder.");
                    }
                });
        });
        
        return ret;
    }
}
