package nodemanager.gui.exportData;

import nodemanager.files.NodeConnFile;
import nodemanager.files.NodeLabelFile;
import nodemanager.files.NodeCoordFile;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import nodemanager.gui.FileSelector;
import nodemanager.files.MapFile;
import java.io.IOException;


/**
 * This is used by EditCanvas to provide options for getting data out of the program
 * @author Matt Crow
 */
public class ExportMenu extends JMenu{
    /**
     * Creates a new ExportMenu
     */
    public ExportMenu(){
        super("Export");
        
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
                    saveCurrentDataset(name, newDir.getAbsolutePath());
                }
        );
    }
    
    private void saveCurrentDataset(String exportName, String parentDir){
        NodeCoordFile coords = new NodeCoordFile(exportName);
        coords.exportData();
        try {
            coords.createFile(parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        NodeConnFile conns = new NodeConnFile(exportName);
        conns.exportData();
        try {
            conns.createFile(parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        NodeLabelFile labels = new NodeLabelFile(exportName);
        labels.exportData();
        try {
            labels.createFile(parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        MapFile map = new MapFile(exportName);
        map.exportData();
        try {
            map.createFile(parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private JMenuItem exportManifest(){
        JMenuItem j = new JMenuItem("Export To The Drive");
        j.addActionListener((ActionEvent e) -> {
            new ExportDialog((JFrame)SwingUtilities.getRoot(this));
        });
        return j;
    }
}
