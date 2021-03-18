package nodemanager.gui.exportData;

import nodemanager.files.NodeConnFileHelper;
import nodemanager.files.NodeLabelFileHelper;
import nodemanager.files.NodeCoordFileHelper;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import nodemanager.gui.FileSelector;
import nodemanager.files.MapFileHelper;
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
        NodeCoordFileHelper coords = new NodeCoordFileHelper(exportName);
        try {
            coords.writeToFileUnderParent(g, parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        NodeConnFileHelper conns = new NodeConnFileHelper(exportName);
        try {
            conns.writeToFileUnderParent(g, parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        NodeLabelFileHelper labels = new NodeLabelFileHelper(exportName);
        try {
            labels.writeToFileUnderParent(g, parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        MapFileHelper map = new MapFileHelper(exportName);
        try {
            map.writeToFileUnderParent(g, parentDir);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        NodeManager.getInstance().getLog().clear();
    }
    
    private JMenuItem exportManifest(){
        JMenuItem j = new JMenuItem("Export To The Drive");
        j.addActionListener((ActionEvent e) -> {
            this.parent.getNodeManagerWindow().getBody().switchToPage(ApplicationBody.REMOTE_EXPORT);
        });
        return j;
    }
}
