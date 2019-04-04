package nodemanager.gui.exportData;

import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;
import nodemanager.Session;
import nodemanager.io.GoogleDriveUploader;
import nodemanager.io.VersionLog;
import nodemanager.io.WayfindingManifest;

/**
 * Acts as the body of the export dialog whenever the user clicks the export to drive button.
 * Allows the user to specify which version of wayfinding they are uploading to, as well as what to name the export.
 * 
 * @author Matt Crow
 */
public class ExportBody extends Container {
    
    private final JTextField name;
    private JTextField toFolder;
    private final JComboBox<String> wayfindingType;
    private final JTextField newType;
    private final JButton exportButton;
    private final JTextArea msg;
    private final VersionLog v;
    
    public ExportBody() {
        super();
        setLayout(new GridLayout(3, 2));
        name = new JTextField("Enter the name for this export");
        add(name);
        
        v = new VersionLog();
        v.download().addOnSucceed((stream)->{
            logDownloaded();
        });
        
        add(createFolderButton());
        
        newType = new JTextField();
        newType.setEditable(false);
        
        wayfindingType = new JComboBox<>();
        wayfindingType.addItemListener((ItemEvent e)->{
            newType.setText(e.getItem().toString());
            newType.setEditable(newType.getText().equals("new type"));
        });
        add(wayfindingType);
        add(newType);
        
        msg = new JTextArea("Please hold while I download the version log...");
        msg.setEditable(false);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(msg);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll);
        
        exportButton = createExportButton();
        add(exportButton);
    }
    
    private Container createFolderButton(){
        Container ret = new Container();
        
        ret.setLayout(new BorderLayout());
        ret.add(new JLabel("Enter which folder to upload to"), BorderLayout.PAGE_START);
        toFolder = new JTextField(GoogleDriveUploader.DEFAULT_FOLDER_ID);
        ret.add(toFolder, BorderLayout.LINE_START);
        JButton verify = new JButton("Verify this folder works");
        verify.addActionListener((e)->{
            msg.setText("Hold on a second...");
            GoogleDriveUploader.isFolder(toFolder.getText())
                    .addOnFail((ex)->msg.setText("Hmm... looks like that file doesn't exist. Could you double check to make sure you have access?"))
                    .addOnSucceed((bool)->{
                        if(bool){
                            msg.setText("Looks like that's a folder! Ready to upload!");
                        } else {
                            msg.setText("Nope, not a folder.");
                        }
                    });
        });
        ret.add(verify, BorderLayout.LINE_END);
        
        return ret;
    }
    
    private JButton createExportButton(){
        JButton ret = new JButton("Export");
        
        ret.addActionListener((ae)->{
            if(wayfindingType.getSelectedItem().equals("new type")){
                v.addType(newType.getText());
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
            
            newMan.upload(toFolder.getText()).addOnSucceed((f)->{
                msg.setText("Upload complete!");
                Session.purgeActions();
                v.addUrl(newType.getText(), newMan.getUrl());
                v.save().addOnFail((err)->{
                    msg.setText(err.getMessage());
                });
            }).addOnFail((ex)->msg.setText(ex.getMessage()));
        });
        
        return ret;
    }
    
    private void logDownloaded(){
        String[] types = v.getTypes();
        String[] options = new String[types.length + 1];
        for(int i = 0; i < types.length; i++){
            options[i] = types[i];
        }
        options[options.length - 1] = "new type";
        for(String option : options){
            wayfindingType.addItem(option);
        }
        
        newType.setText(wayfindingType.getSelectedItem().toString());
        msg.setText("Enter a name for the export, then select what version of wayfinding this is for, then click 'export'");
    }
}
