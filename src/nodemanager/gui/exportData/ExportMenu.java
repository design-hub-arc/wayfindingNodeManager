package nodemanager.gui.exportData;

import nodemanager.gui.exportData.ExportDialog;
import nodemanager.io.NodeConnFile;
import nodemanager.io.NodeLabelFile;
import nodemanager.io.NodeCoordFile;
import nodemanager.io.GoogleDriveUploader;
import nodemanager.io.WayfindingManifest;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import nodemanager.Session;
import nodemanager.gui.FileSelector;
import nodemanager.gui.MapImage;
import nodemanager.io.MapFile;


/**
 * This is used by EditCanvas to provide options for getting data out of the program
 * @author Matt Crow
 */
public class ExportMenu extends JMenu{
    private final MapImage listener;
    
    /**
     * Creates a new ExportMenu, then associates a MapImage with it.
     * @param notify the MapImage to save the image from when the user selects the 
     * "Export map" option.
     */
    public ExportMenu(MapImage notify){
        super("Export");
        
        listener = notify;
        
        add(saveLocal());
        add(exportManifest());
    }
    
    private JMenuItem saveLocal(){
        return new FileSelector(
                "Save to this computer",
                FileSelector.DIR,
                (File f)->{
                    String name = JOptionPane.showInputDialog(this, "What do you want to call this save?");
                    File newDir = new File(f.getAbsoluteFile() + File.separator + name);
                    newDir.mkdir();
                    new NodeCoordFile(name).save(newDir.getAbsolutePath());
                    new NodeConnFile(name).save(newDir.getAbsolutePath());
                    new NodeLabelFile(name).save(newDir.getAbsolutePath());
                    new MapFile(name).save(newDir.getAbsolutePath());
                    //listener.saveImage(name, newDir.getAbsolutePath());
                }
        );
    }
    private JMenuItem exportManifest(){
        JMenuItem j = new JMenuItem("Export To The Drive");
        j.addActionListener((ActionEvent e) -> {
            try{
                Class.forName("com.google.api.client.http.HttpTransport");
                new ExportDialog((JFrame)SwingUtilities.getRoot(this));
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Looks like you forgot to include the lib folder! (See the guide for how to fix)");
            }
        });
        return j;
    }
}
