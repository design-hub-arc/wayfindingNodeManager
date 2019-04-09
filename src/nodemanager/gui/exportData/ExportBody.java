package nodemanager.gui.exportData;

import java.awt.*;
import javax.swing.*;
import nodemanager.Session;
import nodemanager.io.*;

/**
 * Acts as the body of the export dialog whenever the user clicks the export to drive button.
 * Allows the user to specify which version of wayfinding they are uploading to, as well as what to name the export.
 * 
 * @author Matt Crow
 */
public class ExportBody extends Container {
    
    private final JTextField name;
    private final DriveFolderSelector folder;
    private final VersionSelector selectType;
    private final JButton exportButton;
    private final JTextArea msg;
    private static final VersionLog v;
    static {
        v = new VersionLog();
        v.download();
    }
    
    public ExportBody() {
        super();
        setLayout(new GridLayout(5, 1, 10, 10));
        name = new JTextField("Enter the name for this export");
        add(name);
        
        folder = new DriveFolderSelector();
        add(folder);
        
        selectType = new VersionSelector();
        add(selectType);
        
        msg = new JTextArea("");
        msg.setEditable(false);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(msg);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll);
        
        exportButton = createExportButton();
        add(exportButton);
    }
    
    private JButton createExportButton(){
        JButton ret = new JButton("Export");
        
        ret.addActionListener((ae)->{
            if(selectType.getSelectedType().equals(VersionSelector.NEW_TYPE)){
                v.addType(selectType.getSelectedType());
            }
            
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
            
            msg.setText("Beginning upload...");
            revalidate();
            repaint();
            WayfindingManifest newMan = new WayfindingManifest(name.getText());
            
            newMan.upload(folder.getSelectedFolder()).addOnSucceed((f)->{
                msg.setText("Upload complete!");
                Session.purgeActions();
                v.addUrl(selectType.getSelectedType(), newMan.getUrl());
                v.save().addOnFail((err)->{
                    msg.setText(err.getMessage());
                });
            }).addOnFail((ex)->msg.setText(ex.getMessage()));
        });
        
        return ret;
    }
}
