package nodemanager.gui.importData;

import java.awt.event.ActionEvent;
import javax.swing.*;
import nodemanager.*;
import nodemanager.io.*;

/**
 * This is used by EditCanvas to provide options for loading data into the program.
 * @author Matt Crow
 */
public class ImportMenu extends JMenu{
    public ImportMenu(){
        super("Import");
        add(importLocalMenu());
        add(importFromDriveMenu());
        add(resizeMapMenu());
    }
    
    private JMenuItem importLocalMenu(){
        JMenuItem ret = new JMenuItem("Import local files");
        ret.addActionListener((e)->{
            new LocalImportDialog((JFrame)SwingUtilities.getRoot(this));
        });
        return ret;
    }
    
    private JMenuItem importFromDriveMenu(){
        JMenuItem ret = new JMenuItem("Import from the drive");
        ret.addActionListener((e)->{
            try{
                Class.forName("com.google.api.client.http.HttpTransport");
                new DriveImportDialog((JFrame)SwingUtilities.getRoot(this));
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Looks like you forgot to include the lib folder! (See the guide for how to fix)");
            }
            
        });
        return ret;
    }
    
    private JMenuItem resizeMapMenu(){
        JMenuItem resize = new JMenuItem("Resize map image");
        resize.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(null, "Click on a point on the new map to set the new upper-left corner");
            Session.setMode(Mode.RESCALE_UL);    
        });
        return resize;
    }
    
    /**
     * Imports the default data used by the program
     */
    public void loadDefaults() {
        new MapFile().readStream(getClass().getResourceAsStream("/map.png"));
        
        try {
            new NodeCoordFile().readStream(getClass().getResourceAsStream("/nodeData.csv"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Try running clean/build");
        }
        
        try {
            new NodeConnFile().readStream(getClass().getResourceAsStream("/nodeConnections.csv"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Try running clean/build");
        }
        
        try{
            new NodeLabelFile().readStream(getClass().getResourceAsStream("/labels.csv"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Try running clean/build");
        }
    }
}
