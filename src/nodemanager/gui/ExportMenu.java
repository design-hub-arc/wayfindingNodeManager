package nodemanager.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import nodemanager.Session;
import nodemanager.node.Node;
import nodemanager.save.GoogleDriveUploader;
import nodemanager.save.NodeCoordFile;
import nodemanager.save.WayfindingManifest;


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
        
        //add(exportNodeMenu());
        //add(exportLabelMenu());
        //add(exportMapMenu());
        add(saveLocal());
        add(exportManifest());
    }
    /*
    private JMenuItem exportNodeMenu(){
        return new FileSelector(
                "Export Node Data",
                FileSelector.DIR,
                (File f)->{
                    File coordFile = Node.generateCoordFile(f.getAbsolutePath());
                    File connFile = Node.generateConnFile(f.getAbsolutePath());
                    
                    GoogleDriveUploader.uploadCsv(coordFile);
                    GoogleDriveUploader.uploadCsv(connFile);
                }
        );
    }
    
    private JMenuItem exportLabelMenu(){
        return new FileSelector(
                "Export labels",
                FileSelector.DIR,
                (File f)->{
                    File labelFile = Node.generateLabelFile(f.getAbsolutePath());
                    GoogleDriveUploader.uploadCsv(labelFile);
                }
        );
    }
    
    private JMenuItem exportMapMenu(){
        JMenuItem saveMap = new JMenuItem("Export map");
        saveMap.addActionListener((ActionEvent e) -> {
                GoogleDriveUploader.uploadFile(listener.saveImage(), "image/png");
            }
        );
        return saveMap;
    }*/
    private JMenuItem saveLocal(){
        return new FileSelector(
                "Save to this computer",
                FileSelector.DIR,
                (File f)->{
                    String name = JOptionPane.showInputDialog(this, "What do you want to call this save?");
                    new NodeCoordFile(name).save(f.getAbsolutePath());
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
