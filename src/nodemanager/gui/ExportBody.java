package nodemanager.gui;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;
import javax.swing.*;
import nodemanager.Session;
import nodemanager.io.GoogleDriveUploader;
import nodemanager.io.VersionLog;
import nodemanager.io.WayfindingManifest;

/**
 * Acts as the body of the export dialog whenever the user clicks the export to drive button.
 * 
 * TODO: make this modify the version log
 * @author matt
 */
public class ExportBody extends Container {
    
    private JTextField name;
    private JComboBox<String> wayfindingType;
    private JTextField newType;
    private JButton exportButton;
    private final JTextArea msg;
    
    public ExportBody() {
        super();
        setBackground(Color.red);
        setLayout(new FlowLayout());
        name = new JTextField("Enter the name for this export");
        add(name);
        
        VersionLog v = new VersionLog();
        v.download();
        String[] types = v.getTypes();
        String[] options = new String[types.length + 1];
        for(int i = 0; i < types.length; i++){
            options[i] = types[i];
        }
        options[options.length - 1] = "new type";
        wayfindingType = new JComboBox<>(options);
        wayfindingType.addItemListener((ItemEvent e)->{
            newType.setText(e.getItem().toString());
        });
        add(wayfindingType);
        
        newType = new JTextField(wayfindingType.getSelectedItem().toString());
        add(newType);
        
        msg = new JTextArea("Enter a name for the export, then select what version of wayfinding this is for, then click 'export'");
        add(msg);
        
        exportButton = new JButton("Export");
        exportButton.addActionListener((ae)->{
            boolean good = true;
            
            if(wayfindingType.getSelectedItem().equals("new type")){
                v.addType(newType.getText());
            }
            
            try{
                Class.forName("com.google.api.client.http.HttpTransport"); 
                //will throw an error if don't have google drive API
            } catch(ClassNotFoundException ex){
                ex.printStackTrace();
                good = false;
                msg.setText(
                        "Looks like you forgot to include the lib folder \n"
                      + "1. Download lib.zip from the node manager Google drive \n"
                      + "2. Extract the contents so that the lib folder is in the same folder as NodeManager.jar \n"
                      + "3. Click export again"
                );
                return;
            }
            
            msg.setText("Beginning upload...");
            WayfindingManifest newMan = new WayfindingManifest(name.getText());
            
            try{
                newMan.upload(name.getText(), ()->{
                    msg.setText("Upload complete!");
                });
                GoogleDriveUploader.uploadFile(Session.map.saveImage(name.getText()), "image/png", name.getText());
                Session.purgeActions();
                v.addUrl(newType.getText(), newMan.getUrl());
                v.save();
                
            } catch(IOException ex){
                msg.setText(ex.getMessage());
            }
        });
        add(exportButton);
    }
}
