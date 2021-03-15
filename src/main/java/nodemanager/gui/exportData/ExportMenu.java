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
import nodemanager.NodeManager;
import nodemanager.gui.ApplicationBody;
import nodemanager.gui.ApplicationMenuBar;
import nodemanager.gui.InputConsole;
import nodemanager.model.Graph;


/**
 * This is used by EditCanvas to provide options for getting data out of the program
 * @author Matt Crow
 */
public class ExportMenu extends JMenu{
    private final ApplicationMenuBar parent;
    
    /**
     * Creates a new ExportMenu
     * @param parent the menu bar this resides in
     */
    public ExportMenu(ApplicationMenuBar parent){
        super("Export");
        this.parent = parent;
        
        add(saveLocal());
        //add(exportManifest());
    }
    
    private JMenuItem saveLocal(){
        return new FileSelector(
            "Save to this computer",
            FileSelector.DIR,
            (File f)->{
                InputConsole.getInstance().askString(
                    "What do you want to call this save?", 
                    (String name)->{
                        File newDir = new File(f.getAbsoluteFile() + File.separator + name);
                        newDir.mkdir();
                        saveCurrentDataset(name, newDir.getAbsolutePath());
                    }
                );                
            }
        );
    }
    
    private void saveCurrentDataset(String exportName, String parentDir){
        Graph g = NodeManager.getInstance().getGraph();
        NodeCoordFile coords = new NodeCoordFile(exportName);
        coords.exportData(g);
        try {
            coords.createFile(parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        NodeConnFile conns = new NodeConnFile(exportName);
        conns.exportData(g);
        try {
            conns.createFile(parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        NodeLabelFile labels = new NodeLabelFile(exportName);
        labels.exportData(g);
        try {
            labels.createFile(parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        MapFile map = new MapFile(exportName);
        map.exportData(g);
        try {
            map.createFile(parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private JMenuItem exportManifest(){
        JMenuItem j = new JMenuItem("Export To The Drive");
        j.addActionListener((ActionEvent e) -> {
            this.parent.getNodeManagerWindow().getBody().switchToPage(ApplicationBody.REMOTE_EXPORT);
        });
        return j;
    }
}
