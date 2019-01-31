package nodemanager.gui;

import nodemanager.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
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
        
        Session.currentPanel = this;
        
        GridBagLayout lo = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(lo);
        
        menu = new JMenuBar();
        menu.setMinimumSize(new Dimension(50, 50));
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        add(menu, c);
        menu.setLayout(new FlowLayout());
        
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
        sideBar.add(Session.controlList);
        
        body.setLayout(new GridLayout(1, 1));
        map = new MapImage();
        body.add(map);
        
        JMenu importMenu = createImportMenu();
        menu.add(importMenu);
        
        JMenu exportMenu = createExportMenu();
        menu.add(exportMenu);
        
        JMenu optionMenu = createOptionMenu();
        menu.add(optionMenu);
        
        JMenuItem addNodeButton = new JMenuItem("Add a new Node");
        addNodeButton.addActionListener((ActionEvent e) -> {
                Session.mode = Mode.ADD;
                JOptionPane.showMessageDialog(null, "Click on any location on the map to add a new node");
            });
        menu.add(addNodeButton);
        
        JMenuItem resetData = new JMenuItem("Clear all data");
        resetData.addActionListener((ActionEvent e) -> {
            map.removeAllNodes();
            Node.removeAll();
            map.addNode(new Node(-1, 0, 0));
            map.addNode(new Node(-2, 100, 100));
            map.scaleTo(0, 0, 100, 100);
            map.setImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)); //dummy image to prevent NullPointerException
        });
        menu.add(resetData);
        
        setBackground(Color.blue);
        revalidate();
        repaint();
        
        
        loadDefaults();
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
        
        menu.add(createSelector("node file", 
                new String[]{"Comma Separated Values", "csv"}, (File f) -> {
                    try {
                        loadNodesFromFile(new FileInputStream(f));
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
        }));
        
        menu.add(createSelector("connection file",
                new String[]{"Comma Separated Values", "csv"}, (File f) -> {
                    try {
                        loadConn(new FileInputStream(f));
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
        }));
        
        menu.add(createSelector(
                "map Image",
                new String[]{"Image file", "JPEG file", "jpg", "jpeg", "png"},
                (File f) -> {
                    try {
                        map.setImage(ImageIO.read(f));
                        repaint();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        ));
        
        JMenuItem resize = new JMenuItem("Resize map image");
        resize.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(null, "Click on a point on the new map to set the new upper-left corner");
            Session.mode = Mode.RESCALE_UL;
        });
        menu.add(resize);
        
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
    
    
    private JMenu createOptionMenu() {
        JMenu m = new JMenu("Options");
        
        JMenuItem chooseNodeSize = new JMenuItem("Change node icon size");
        chooseNodeSize.addActionListener((ActionEvent e) -> {
            try{
                NodeIcon.setSize(Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new size for node icons:")));
            } catch(NumberFormatException ex){
                //just ignore it
            }
        });
        m.add(chooseNodeSize);
        
        JMenuItem choosePanSpeed = new JMenuItem("Change pan speed");
        choosePanSpeed.addActionListener((ActionEvent e) -> {
            try{
                map.setPanSpeed(Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new panning speed (5-10 recommended):")));
            } catch(NumberFormatException ex){
                //just ignore it
            }
        });
        m.add(choosePanSpeed);
        
        JMenuItem chooseZoomSpeed = new JMenuItem("Change zoom speed");
        chooseZoomSpeed.addActionListener((ActionEvent e) -> {
            try{
                int perc = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new zooming speed (1-10 recommended):"));
                map.setZoomSpeed(0.01 * perc);
            } catch(NumberFormatException ex){
                //just ignore it
            }
        });
        m.add(chooseZoomSpeed);
        
        return m;
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

    
    private void loadDefaults(){
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
     * Used by createSelector to react to choosing a file through a JFileChooser
     */
    private interface FileSelectedListener{
        public abstract void run(File f);
    }
}