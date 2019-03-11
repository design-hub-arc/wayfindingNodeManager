package nodemanager.gui;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import nodemanager.*;
import nodemanager.io.MapFile;
import nodemanager.node.*;
import nodemanager.io.*;

/**
 * This is used by EditCanvas to provide options for loading data into the program.
 * @author Matt Crow
 */
public class ImportMenu extends JMenu{

    
    private final MapImage listener; 
    
    /**
     * Creates a new ImportMenu, then associates a MapImage with it.
     * @param notify the MapImage to notify of any imports made by this
     */
    public ImportMenu(MapImage notify){
        super("Import");
        
        listener = notify;
        
        add(importMapMenu());
        add(resizeMapMenu());
        add(importNodeMenu());
        add(importConnMenu());
        add(importLabelMenu());
        add(importFromDriveMenu());
    }
    
    private JMenuItem importMapMenu(){
        return new FileSelector("Select map image", 
            new String[]{"Image file", "JPEG file", "jpg", "jpeg", "png"},
            (File f) -> {
                try {
                    importMap(new FileInputStream(f));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        );
    }
    
    //until we get MapFile working, we need to keep this
    private void importMap(InputStream s) throws IOException{
        //new MapFile().readStream(new FileInputStream(f));
        listener.setImage(ImageIO.read(s));
    }
    
    
    private JMenuItem resizeMapMenu(){
        JMenuItem resize = new JMenuItem("Resize map image");
        resize.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(null, "Click on a point on the new map to set the new upper-left corner");
            Session.setMode(Mode.RESCALE_UL);    
        });
        return resize;
    }
    
    
    private JMenuItem importNodeMenu(){
        return new FileSelector("Select Node File", FileSelector.CSV, (File f) -> {
            try {
                importNodes(new FileInputStream(f));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
    }
    private void importNodes(InputStream s){
        Node.removeAll();
        new NodeCoordFile().readStream(s);
        listener.refreshNodes();
    }
    
    
    private JMenuItem importConnMenu(){
        return new FileSelector("Select connection file", FileSelector.CSV, (File f) -> {
            try {
                importConns(new FileInputStream(f));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
    }
    private void importConns(InputStream s){
        new NodeConnFile().readStream(s);
    }
    
    
    private JMenuItem importLabelMenu(){
        return new FileSelector("Select label file", FileSelector.CSV, (File f) -> {
            try {
                importLabels(new FileInputStream(f));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
    }
    private void importLabels(InputStream s){
        new NodeLabelFile().readStream(s);
    }
    
    
    private JMenuItem importFromDriveMenu(){
        JMenuItem ret = new JMenuItem("Import from the drive");
        ret.addActionListener((e)->{
            try{
                Class.forName("com.google.api.client.http.HttpTransport");
                new ImportDialog((JFrame)SwingUtilities.getRoot(this));
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Looks like you forgot to include the lib folder! (See the guide for how to fix)");
            }
            
        });
        return ret;
    }
    
    
    /**
     * Imports the default data used by the program
     */
    public void loadDefaults() {
        try {
            importMap(getClass().getResourceAsStream("/map.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Try running clean/build");
        }
        
        try {
            importNodes(getClass().getResourceAsStream("/nodeData.csv"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Try running clean/build");
        }
        
        try {
            importConns(getClass().getResourceAsStream("/nodeConnections.csv"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Try running clean/build");
        }
        
        try{
            importLabels(getClass().getResourceAsStream("/labels.csv"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Try running clean/build");
        }
    }
}
