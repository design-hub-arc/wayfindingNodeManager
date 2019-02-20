package nodemanager.gui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import nodemanager.node.Node;
import nodemanager.save.GoogleDriveUploader;


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
        
        add(exportNodeMenu());
        add(exportLabelMenu());
        add(exportMapMenu());
    }
    
    private JMenuItem exportNodeMenu(){
        JMenuItem exportNodeData = new JMenuItem("Export Node Data");
        exportNodeData.addActionListener((ActionEvent ae) -> {
            JFileChooser destination = new JFileChooser();
            destination.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int response = destination.showOpenDialog(destination);
            if (response == JFileChooser.APPROVE_OPTION) {
                File f = destination.getSelectedFile(); //this is a directory
                
                File coordFile = Node.generateCoordFile(f.getAbsolutePath());
                File connFile = Node.generateConnFile(f.getAbsolutePath());
                
                GoogleDriveUploader.uploadFile(coordFile);
                GoogleDriveUploader.uploadFile(connFile);
                
                
                
            }
        });
        return exportNodeData;
    }
    
    private JMenuItem exportLabelMenu(){
        JMenuItem exportLabels = new JMenuItem("Export labels");
        exportLabels.addActionListener((ActionEvent ae) -> {
            JFileChooser destination = new JFileChooser();
            destination.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int response = destination.showOpenDialog(destination);
            if (response == JFileChooser.APPROVE_OPTION) {
                File f = destination.getSelectedFile();
                Node.generateLabelFile(f.getAbsolutePath());
            }
        });
        return exportLabels;
    }
    
    private JMenuItem exportMapMenu(){
        JMenuItem saveMap = new JMenuItem("Export map");
        saveMap.addActionListener((ActionEvent e) -> listener.saveImage());
        return saveMap;
    }
}
