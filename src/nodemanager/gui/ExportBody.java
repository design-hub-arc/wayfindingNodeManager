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
        
        exportButton = new JButton("Export");
        exportButton.addActionListener((ae)->{
            if(wayfindingType.getSelectedItem().equals("new type")){
                v.addType(newType.getText());
            }
            
            try{
                Class.forName("com.google.api.client.http.HttpTransport"); //will throw if don't have google drive API
                JOptionPane.showMessageDialog(this, "Beginning upload...");
                new WayfindingManifest(name.getText()).upload(name.getText(), ()->{
                    JOptionPane.showMessageDialog(this, "Upload complete!");
                });
                GoogleDriveUploader.uploadFile(Session.map.saveImage(name.getText()), "image/png", name.getText());
                Session.purgeActions();
            } catch(ClassNotFoundException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this, 
                        "An error occured while uploading to the drive (did you remember the lib folder?), so you need to save a local copy", 
                        "Not good!", 
                        JOptionPane.ERROR_MESSAGE
                );
                System.err.println("not done with ExportMenu.exportManifest");
            } catch(IOException ex){
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                        this,
                        ex.getMessage(),
                        "Not good!",
                        JOptionPane.ERROR_MESSAGE
                );
            }
            
            v.addUrl(newType.getText(), "how do I get the URL?");
            
            close();
        });
        add(exportButton);
    }
    
    private void close(){
        ((JDialog)SwingUtilities.getRoot(this)).dispose();
    }
    
}
