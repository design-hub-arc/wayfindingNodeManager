package nodemanager.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import nodemanager.Session;
import nodemanager.save.*;


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
                    listener.saveImage();
                }
        );
    }
    private JMenuItem exportManifest(){
        return new FileSelector(
                "Export everything",
                FileSelector.DIR,
                (File f)->{
                    String folderName = JOptionPane.showInputDialog(this, "What do you want to call this import?");
                    GoogleDriveUploader.uploadCsv(new WayfindingManifest(folderName).export(f.getAbsolutePath()), folderName);
                    GoogleDriveUploader.uploadFile(listener.saveImage(), "image/png", folderName);
                    Session.purgeActions();
                }
        );
    }
}
