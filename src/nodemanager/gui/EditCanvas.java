package nodemanager.gui;

import nodemanager.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import nodemanager.node.*;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class EditCanvas extends JPanel {

    /**
     * EditCanvas is the main JPanel used by the program
     */
    private final JMenuBar menu;
    private final JComponent body;
    private final Sidebar sideBar;

    private final NodeDataPane selectedNode;
    private MapImage map;

    /**
     * Creates many different components, then adds them to the JPanel.
     */
    public EditCanvas() {
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

        body = new JComponent() {
        };
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
        sideBar.add(Session.CONTROL_LIST);
        sideBar.add(Session.MODE_LABEL);

        body.setLayout(new GridLayout(1, 1));
        map = new MapImage();
        body.add(map);

        JMenu importMenu = createImportMenu();
        menu.add(importMenu);

        JMenu exportMenu = createExportMenu();
        menu.add(exportMenu);
        
        menu.add(createSelectMenu());
        
        JMenu optionMenu = createOptionMenu();
        menu.add(optionMenu);

        JMenuItem addNodeButton = new JMenuItem("Add a new Node");
        addNodeButton.addActionListener((ActionEvent e) -> {
            Session.setMode(Mode.ADD);
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
        
        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener((ActionEvent e) -> {
            Session.undoLastAction();
        });
        menu.add(undo);

        setBackground(Color.blue);
        revalidate();
        repaint();

        loadDefaults();
    }

    /**
     * Creates the menu used to load data into the program. Note that it doesn't
     * add the menu automatically.
     *
     * Move to another class in the future?
     *
     * @return the created menu.
     */
    private JMenu createImportMenu() {
        JMenu menu = new JMenu("Import");
        String[] csv = new String[]{"Comma Separated Values", "csv"};
        
        menu.add(new FileSelector("Select Node File", csv, (File f) -> {
            try {
                loadNodesFromFile(new FileInputStream(f));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }));
        

        menu.add(new FileSelector("Select connection file", csv, (File f) -> {
            try {
                NodeParser.parseConnFile(new FileInputStream(f));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }));

        menu.add(new FileSelector("Select label file", csv, (File f) -> {
            try {
                NodeParser.parseTitleFile(new FileInputStream(f));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }));

        menu.add(new FileSelector("Select map image", 
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
            Session.setMode(Mode.RESCALE_UL);    
        });
        menu.add(resize);

        return menu;
    }

    /**
     * Creates the menu used to get data out of the program. Note that it
     * doesn't add the menu automatically.
     *
     * Move to another class in the future?
     *
     * @return the created menu.
     */
    private JMenu createExportMenu() {
        JMenu menu = new JMenu("Export");

        JMenuItem exportNodeData = new JMenuItem("Export Node Data");
        exportNodeData.addActionListener((ActionEvent ae) -> {
            JFileChooser destination = new JFileChooser();
            destination.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int response = destination.showOpenDialog(destination);
            if (response == JFileChooser.APPROVE_OPTION) {
                File f = destination.getSelectedFile();
                Node.generateDataAt(f.getAbsolutePath());
            }
        });
        menu.add(exportNodeData);
        
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
        menu.add(exportLabels);
        
        JMenuItem saveMap = new JMenuItem("Export map");
        saveMap.addActionListener((ActionEvent e) -> map.saveImage());
        menu.add(saveMap);

        return menu;
    }
    
    private JMenu createSelectMenu(){
        JMenu m = new JMenu("Find a node");
        
        JMenuItem byId = new JMenuItem("...by id");
        byId.addActionListener((e) -> {
            try{
                int id = Integer.parseInt(JOptionPane.showInputDialog("Enter the id of the node you want to find: "));
            
                Node find = Node.get(id);
                if(find == null){
                    JOptionPane.showMessageDialog(this, "Couldn't find a node with an id of " + id);
                } else {
                    selectedNode.selectNode(find);
                }
            } catch(NumberFormatException ex){
                //do nothing
            }
        });
        m.add(byId);
        
        JMenuItem byLabel = new JMenuItem("...by label");
        byLabel.addActionListener((e) -> {
            String label = JOptionPane.showInputDialog("Enter a label of the node you want to find: ");
            Node find = Node.get(label);
            if(find == null){
                JOptionPane.showMessageDialog(menu, "Cannot find node with label " + label);
            } else {
                selectedNode.selectNode(find);
            }
        });
        m.add(byLabel);
        
        return m;
    }

    private JMenu createOptionMenu() {
        JMenu m = new JMenu("Options");

        JMenuItem chooseNodeSize = new JMenuItem("Change node icon size");
        chooseNodeSize.addActionListener((e) -> {
            try {
                NodeIcon.setSize(Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new size for node icons:")));
            } catch (NumberFormatException ex) {
                //just ignore it
            }
        });
        m.add(chooseNodeSize);

        JMenuItem choosePanSpeed = new JMenuItem("Change pan speed");
        choosePanSpeed.addActionListener((ActionEvent e) -> {
            try {
                map.setPanSpeed(Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new panning speed (5-10 recommended):")));
            } catch (NumberFormatException ex) {
                //just ignore it
            }
        });
        m.add(choosePanSpeed);

        JMenuItem chooseZoomSpeed = new JMenuItem("Change zoom speed");
        chooseZoomSpeed.addActionListener((ActionEvent e) -> {
            try {
                int perc = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter new zooming speed (1-10 recommended):"));
                map.setZoomSpeed(0.01 * perc);
            } catch (NumberFormatException ex) {
                //just ignore it
            }
        });
        m.add(chooseZoomSpeed);
        
        JMenuItem showAllConn = new JMenuItem("Draw all connections");
        showAllConn.addActionListener((e) -> {
            Node.getAll().forEach(node -> node.getIcon().setDrawLinks(true));
        });
        m.add(showAllConn);
        
        JMenuItem hideAllConn = new JMenuItem("Hide all connections");
        hideAllConn.addActionListener((e) -> {
            Node.getAll().forEach(node -> node.getIcon().setDrawLinks(false));
        });
        m.add(hideAllConn);

        return m;
    }

    /**
     * Imports node position data from a csv file, then adds their icons to the
     * map
     *
     * @param i the InputStream given by a FileInputStream generated from a csv
     * file
     */
    private void loadNodesFromFile(InputStream i) {
        map.removeAllNodes();
        Node.removeAll();

        NodeParser.parseNodeFile(i);
        map.scaleTo(Node.get(-1).rawX, Node.get(-1).rawY, Node.get(-2).rawX, Node.get(-2).rawY);

        Node.getAll().forEach((n) -> map.addNode(n));
        revalidate();
        repaint();
    }

    private void loadDefaults() {
        //set defaults
        try {
            map.setImage(ImageIO.read(getClass().getResourceAsStream("/map.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            loadNodesFromFile(getClass().getResourceAsStream("/nodeData.csv"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            NodeParser.parseConnFile(getClass().getResourceAsStream("/nodeConnections.csv"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try{
            NodeParser.parseTitleFile(getClass().getResourceAsStream("/labels.csv"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
