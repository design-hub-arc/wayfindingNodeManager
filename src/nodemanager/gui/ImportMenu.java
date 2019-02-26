package nodemanager.gui;

import java.awt.event.ActionEvent;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import nodemanager.*;
import nodemanager.node.*;

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
    }
    
    private JMenuItem importMapMenu(){
        return new FileSelector("Select map image", 
            new String[]{"Image file", "JPEG file", "jpg", "jpeg", "png"},
            (File f) -> {
                try {
                    listener.setImage(ImageIO.read(f));
                    repaint();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        );
    }
    private void importMap(InputStream s) throws IOException{
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
        NodeParser.parseNodeFile(s);
        listener.refreshNodes();
    }
    
    
    private JMenuItem importConnMenu(){
        return new FileSelector("Select connection file", FileSelector.CSV, (File f) -> {
            try {
                NodeParser.parseConnFile(new FileInputStream(f));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
    }
    private void importConns(InputStream s){
        NodeParser.parseConnFile(s);
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
        NodeParser.parseTitleFile(s, "");
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
