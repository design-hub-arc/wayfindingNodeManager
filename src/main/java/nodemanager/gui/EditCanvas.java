package nodemanager.gui;

import nodemanager.gui.exportData.ExportMenu;
import nodemanager.gui.importData.ImportMenu;
import nodemanager.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import nodemanager.modes.ModeNewNode;
import nodemanager.node.*;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class EditCanvas extends ApplicationPage {

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
    public EditCanvas(ApplicationBody parent) {
        super(parent);

        Session.currentPanel = this;
        
        setLayout(new BorderLayout());
        
        menu = new JMenuBar();        
        add(menu, BorderLayout.PAGE_START);
        menu.setLayout(new FlowLayout());

        JSplitPane content = new JSplitPane();
        content.setContinuousLayout(true);
        add(content, BorderLayout.CENTER);
        
        body = new JComponent() {
        };
        body.setLayout(new FlowLayout());
        content.setRightComponent(body);
        
        sideBar = new Sidebar();
        content.setLeftComponent(sideBar);

        selectedNode = new NodeDataPane();
        sideBar.add(selectedNode);
        Session.dataPane = selectedNode;
        sideBar.add(Session.CONTROL_LIST);
        sideBar.add(Session.MODE_LABEL);

        body.setLayout(new GridLayout(1, 1));
        map = new MapImage();
        body.add(map);
        
        ImportMenu importMenu = new ImportMenu();
        menu.add(importMenu);

        menu.add(new ExportMenu());
        
        menu.add(createSelectMenu());
        
        JMenu optionMenu = createOptionMenu();
        menu.add(optionMenu);

        JMenuItem addNodeButton = new JMenuItem("Add a new Node");
        addNodeButton.addActionListener((ActionEvent e) -> {
            Session.setMode(Mode.ADD);
        });
        menu.add(addNodeButton);

        JMenuItem resetData = new JMenuItem("Clear all data");
        resetData.addActionListener((ActionEvent e) -> {
            resetData();
        });
        menu.add(resetData);
        
        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener((ActionEvent e) -> {
            Session.undoLastAction();
        });
        menu.add(undo);
        
        JMenuItem redo = new JMenuItem("Redo");
        redo.addActionListener((ActionEvent e) -> {
            Session.redoLastAction();
        });
        menu.add(redo);
        
        
        Session.registerControl(KeyStroke.getKeyStroke("control Z"), ()->{
            for(ActionListener al : undo.getActionListeners()){
                al.actionPerformed(null);
            }
        }, "undo");
        
        Session.registerControl(KeyStroke.getKeyStroke("control Y"), ()->{
            for(ActionListener al : redo.getActionListeners()){
                al.actionPerformed(null);
            }
        }, "redo");

        setBackground(Color.blue);
        resetData();
        revalidate();
        repaint();
    }
    
    private void resetData(){
        map.removeAllNodes();
        Node.removeAll();
        map.addNode(Node.updateNode(-1, 0, 0));
        map.addNode(Node.updateNode(-2, 100, 100));
        map.scaleTo(0, 0, 100, 100);
        map.setImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)); //dummy image to prevent NullPointerException
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
}
