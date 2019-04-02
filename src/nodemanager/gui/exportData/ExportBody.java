package nodemanager.gui.exportData;

import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;
import nodemanager.Session;
import nodemanager.io.VersionLog;
import nodemanager.io.WayfindingManifest;

/**
 * Acts as the body of the export dialog whenever the user clicks the export to drive button.
 * Allows the user to specify which version of wayfinding they are uploading to, as well as what to name the export.
 * 
 * @author Matt Crow
 */
public class ExportBody extends Container {
    
    private JTextField name;
    private JComboBox<String> wayfindingType;
    private JTextField newType;
    private JButton exportButton;
    private final JTextArea msg;
    private final VersionLog v;
    
    public ExportBody() {
        super();
        setLayout(new GridLayout(5, 1));
        name = new JTextField("Enter the name for this export");
        add(name);
        
        v = new VersionLog();
        v.download().addOnSucceed((stream)->{
            logDownloaded();
        });
        
        wayfindingType = new JComboBox<>();
        wayfindingType.addItemListener((ItemEvent e)->{
            newType.setText(e.getItem().toString());
            newType.setEditable(newType.getText().equals("new type"));
        });
        add(wayfindingType);
        
        newType = new JTextField();
        newType.setEditable(false);
        add(newType);
        
        msg = new JTextArea("Please hold while I download the version log...");
        msg.setEditable(false);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        
        JScrollPane scroll = new JScrollPane(msg);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scroll);
        
        exportButton = new JButton("Export");
        exportButton.addActionListener((ae)->{
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
            
            newMan.upload(name.getText()).addOnSucceed((f)->{
                msg.setText("Upload complete!");
                Session.purgeActions();
                v.addUrl(newType.getText(), newMan.getUrl());
                v.save().addOnFail((err)->{
                    msg.setText(err.getMessage());
                });
            }).addOnFail((ex)->msg.setText(ex.getMessage()));
        });
        add(exportButton);
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
