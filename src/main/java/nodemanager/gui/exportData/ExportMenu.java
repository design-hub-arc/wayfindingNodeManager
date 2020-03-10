package nodemanager.gui.exportData;

import nodemanager.io.NodeConnFile;
import nodemanager.io.NodeLabelFile;
import nodemanager.io.NodeCoordFile;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import nodemanager.gui.FileSelector;
import nodemanager.io.MapFile;


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
                    new NodeCoordFile(name).save(newDir.getAbsolutePath());
                    new NodeConnFile(name).save(newDir.getAbsolutePath());
                    new NodeLabelFile(name).save(newDir.getAbsolutePath());
                    new MapFile(name).save(newDir.getAbsolutePath());
                }
        );
    }
    private JMenuItem exportManifest(){
        JMenuItem j = new JMenuItem("Export To The Drive");
        j.addActionListener((ActionEvent e) -> {
            try{
                Class.forName("com.google.api.client.http.HttpTransport");
                new ExportDialog((JFrame)SwingUtilities.getRoot(this));
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Looks like you forgot to include the lib folder! (See the guide for how to fix)");
            }
        });
        return j;
    }
}
