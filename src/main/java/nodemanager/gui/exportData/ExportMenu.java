package nodemanager.gui.exportData;

import files.NodeConnFile;
import files.NodeLabelFile;
import files.NodeCoordFile;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import nodemanager.gui.FileSelector;
import files.MapFile;
import io.LocalFileWriter;
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
            LocalFileWriter.createFileFor(coords, parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        NodeConnFile conns = new NodeConnFile(exportName);
        conns.exportData();
        try {
            LocalFileWriter.createFileFor(conns, parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        NodeLabelFile labels = new NodeLabelFile(exportName);
        labels.exportData();
        try {
            LocalFileWriter.createFileFor(labels, parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        MapFile map = new MapFile(exportName);
        map.exportData();
        try {
            LocalFileWriter.createFileFor(map, parentDir);
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
