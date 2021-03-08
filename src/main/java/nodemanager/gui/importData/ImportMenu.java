package nodemanager.gui.importData;

import java.awt.event.ActionEvent;
import javax.swing.*;
import nodemanager.*;
import nodemanager.gui.InputConsole;

/**
 * This is used by EditCanvas to provide options for loading data into the program.
 * @author Matt Crow
 */
public class ImportMenu extends JMenu{
    public ImportMenu(){
        super("Import");
        add(importLocalMenu());
        //add(importFromDriveMenu());
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
                InputConsole.getInstance().warn("Couldn't find the Google Drive API");
            }
            
        });
        return ret;
    }
    
    private JMenuItem resizeMapMenu(){
        JMenuItem resize = new JMenuItem("Resize map image");
        resize.addActionListener((ActionEvent e) -> {
            Session.setMode(Mode.RESCALE_UL);    
        });
        return resize;
    }
}
