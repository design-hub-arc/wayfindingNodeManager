package nodemanager.gui;

import nodemanager.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import nodemanager.node.*;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */

public class EditCanvas extends JPanel{
    /**
    EditCanvas is the main JPanel used by the program
    */
    private final JMenuBar menu;
    private final JComponent body;
    private final Sidebar sideBar;
    
    private final NodeDataPane selectedNode;
    private MapImage map;
    
    /**
    * Creates many different components, then adds them to the JPanel.
    */
    public EditCanvas(){
        
        super();
        
        GridBagLayout lo = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(lo);
        
        menu = new JMenuBar();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        add(menu, c);
        
        body = new JComponent(){};
        body.setLayout(new FlowLayout());
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 9;
        c.weighty = 9;
        c.fill = GridBagConstraints.BOTH;
        add(body, c);
        
        sideBar = new Sidebar();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 9;
        c.fill = GridBagConstraints.BOTH;
        add(sideBar, c);
        
        
        selectedNode = new NodeDataPane();
        sideBar.add(selectedNode);
        Session.dataPane = selectedNode;
        
        body.setLayout(new GridLayout(1, 1));
        map = new MapImage();
        body.add(map);
        
        JMenu importMenu = createImportMenu();
        menu.add(importMenu);
        
        JMenu exportMenu = createExportMenu();
        menu.add(exportMenu);
        
        JMenuItem addNodeButton = new JMenuItem("Add a new Node");
        addNodeButton.addActionListener((ActionEvent e) -> {
                Session.mode = Mode.ADD;
                JOptionPane.showMessageDialog(null, "Click on any location on the map to add a new node");
            });
        menu.add(addNodeButton);
        
        setBackground(Color.blue);
        
        
        
        //set defaults
        try{
            map.setImage(ImageIO.read(getClass().getResourceAsStream("/map.png")));
        } catch(IOException e){
            e.printStackTrace();
        }
        
        try{
            loadNodesFromFile(getClass().getResourceAsStream("/nodeData.csv"));
        } catch(Exception e){
            e.printStackTrace();
        }
        
        try{
           loadConn(getClass().getResourceAsStream("/nodeConnections.csv"));
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Creates the menu used to load data into the program.
     * Note that it doesn't add the menu automatically.
     * 
     * Move to another class in the future?
     * @return the created menu.
     */
    private JMenu createImportMenu(){
        JMenu menu = new JMenu("Import");
        
        menu.add(createSelector(
                "node file", 
                new String[]{"Comma Separated Values", "csv"},
                new FileSelectedListener(){
                    @Override
                    public void run(File f){
                        try {
                            loadNodesFromFile(new FileInputStream(f));
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        ));
        
        menu.add(createSelector(
                "connection file",
                new String[]{"Comma Separated Values", "csv"},
                new FileSelectedListener(){
                    @Override
                    public void run(File f){
                        try {
                            loadConn(new FileInputStream(f));
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        ));
        
        menu.add(createSelector(
                "map Image", 
                new String[]{"Image file", "JPEG file", "jpg", "jpeg", "png"},
                new FileSelectedListener(){
                    @Override
                    public void run(File f){
                        try {
                            map.setImage(ImageIO.read(f));
                            JOptionPane.showMessageDialog(null, "Click on a point on the new map to set the new upper-left corner");
                            Session.mode = Mode.RESCALE_UL;
                            repaint();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
        ));
        
        return menu;
    }
    
    
    /**
     * Creates the menu used to get data out of the program.
     * Note that it doesn't add the menu automatically.
     * 
     * Move to another class in the future?
     * @return the created menu.
     */
    private JMenu createExportMenu(){
        JMenu menu = new JMenu("Export");
        
        JMenuItem exportNodeData = new JMenuItem("Export Node Data");
        exportNodeData.addActionListener((ActionEvent ae) -> {
                JFileChooser destination = new JFileChooser();
                destination.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int response = destination.showOpenDialog(destination);
                if(response == JFileChooser.APPROVE_OPTION){
                    File f = destination.getSelectedFile();
                    Node.generateDataAt(f.getAbsolutePath());
                }
            });
        menu.add(exportNodeData);
        
        JMenuItem saveMap = new JMenuItem("Export map");
        saveMap.addActionListener((ActionEvent e) -> map.saveImage());
        menu.add(saveMap);
        
        return menu;
    }
    
    /**
     * Creates a button that allows the user to select a file with a given extention, 
     * then invokes the given file listener's run method, passing in the file selected as a parameter
     * 
     * @param type the caption on the button this will create
     * @param types the file types the selector allows the user to select
     * @param l the FileSelectedListener (see the bottom of this file) that will run after the user chooses a file
     * @return the JButton constructed
     */
    private JMenuItem createSelector(String type, String[] types, FileSelectedListener l){
        JMenuItem ret = new JMenuItem("Select " + type);
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter(type, types));
        
        ret.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                int response = chooser.showOpenDialog(chooser);
                if(response == JFileChooser.APPROVE_OPTION){
                    l.run(chooser.getSelectedFile());
                }
            }
        });
        
        return ret;
    }
    
    /**
     * Used to move some of the cluttered code out of the constructor.
     * Creates a menu item which, when clicked, allows the user to choose
     * a directory to export the data created by the program to.
     * @return  the menu item constructed
     */
    private JMenuItem createExportButton(){
        JMenuItem j = new JMenuItem("Export node data");
        j.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser destination = new JFileChooser();
                destination.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int response = destination.showOpenDialog(destination);
                if(response == JFileChooser.APPROVE_OPTION){
                    File f = destination.getSelectedFile();
                    Node.generateDataAt(f.getAbsolutePath());
                }
            }
        });
        
        return j;
    }
    
    /**
     * Imports node position data from a csv file,
     * then adds their icons to the map
     * @param i the InputStream given by a FileInputStream generated from a csv file
     */
    private void loadNodesFromFile(InputStream i){
        map.removeAllNodes();
        Node.removeAll();
        
        NodeParser.parseNodeFile(i);
        map.scaleTo(Node.get(-1).rawX, Node.get(-1).rawY, Node.get(-2).rawX, Node.get(-2).rawY);
        
        Node.getAll().forEach((n) -> map.addNode(n));
        revalidate();
        repaint();
    }
    
    /**
     * Imports node connection data from a csv file
     * @param i the InputStream given by a FileInputStream generated from a csv file
     */
    private void loadConn(InputStream i){
        NodeParser.parseConnFile(i);
    }
    
    /**
     * Used by createSelector to react to choosing a file through a JFileChooser
     */
    private abstract class FileSelectedListener{
        public abstract void run(File f);
    }
}