package nodemanager.gui.importData;

import java.awt.event.ActionEvent;
import javax.swing.*;
import nodemanager.*;
import nodemanager.gui.ApplicationBody;
import nodemanager.gui.ApplicationMenuBar;
import nodemanager.gui.InputConsole;

/**
 * This is used by EditCanvas to provide options for loading data into the program.
 * @author Matt Crow
 */
public class ImportMenu extends JMenu{
    private final ApplicationMenuBar parent;
    
    public ImportMenu(ApplicationMenuBar parent){
        super("Import");
        this.parent = parent;
        
        add(importLocalMenu());
        //add(importFromDriveMenu());
        add(resizeMapMenu());
    }
    
    public final ApplicationMenuBar getMenuBar(){
        return parent;
    }
    
    private JMenuItem importLocalMenu(){
        JMenuItem ret = new JMenuItem("Import local files");
        ret.addActionListener((e)->{
            parent.getNodeManagerWindow().getBody().switchToPage(ApplicationBody.LOCAL_IMPORT);
        });
        return ret;
    }
    
    private JMenuItem importFromDriveMenu(){
        JMenuItem ret = new JMenuItem("Import from the drive");
        ret.addActionListener((e)->{
            try{
                Class.forName("com.google.api.client.http.HttpTransport");
                parent.getNodeManagerWindow().getBody().switchToPage(ApplicationBody.REMOTE_IMPORT);
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
