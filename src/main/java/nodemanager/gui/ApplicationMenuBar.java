package nodemanager.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import nodemanager.Mode;
import nodemanager.Session;
import nodemanager.gui.exportData.ExportMenu;
import nodemanager.gui.importData.ImportMenu;
import nodemanager.node.Node;

/**
 *
 * @author Matt
 */
public class ApplicationMenuBar extends JMenuBar {
    public ApplicationMenuBar(){
        super();
        
        add(new ImportMenu());
        add(new ExportMenu());
        add(createSelectMenu());
        add(createOptionMenu());
        
        JMenuItem addNodeButton = new JMenuItem("Add a new Node");
        addNodeButton.addActionListener((ActionEvent e) -> {
            Session.setMode(Mode.ADD);
        });
        add(addNodeButton);
        
        JMenuItem resetData = new JMenuItem("Clear all data");
        resetData.addActionListener((ActionEvent e) -> {
            resetData();
        });
        add(resetData);
        
        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener((ActionEvent e) -> {
            Session.undoLastAction();
        });
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        add(undo);
        
        JMenuItem redo = new JMenuItem("Redo");
        redo.addActionListener((ActionEvent e) -> {
            Session.redoLastAction();
        });
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        add(redo);
        
        resetData();
    }
    
    private void resetData(){
        Session.map.removeAllNodes();
        Node.removeAll();
        Session.map.addNode(Node.updateNode(-1, 0, 0));
        Session.map.addNode(Node.updateNode(-2, 100, 100));
        Session.map.scaleTo(0, 0, 100, 100);
        Session.map.setImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)); //dummy image to prevent NullPointerException
    }
    
    private void trySelectNode(int id){
        Node find = Node.get(id);
        if(find == null){
            InputConsole.getInstance().warn("Couldn't find a node with an id of " + id);
        } else {
            Session.dataPane.selectNode(find);
        }
    }
    
    private void trySelectNode(String label){
        Node find = Node.get(label);
        if(find == null){
            InputConsole.getInstance().warn("Cannot find node with label " + label);
        } else {
            Session.dataPane.selectNode(find);
        }
    }
    
    private JMenu createSelectMenu(){
        JMenu m = new JMenu("Find a node");
        
        JMenuItem byId = new JMenuItem("...by id");
        byId.addActionListener((e) -> {
            InputConsole.getInstance().askInt(
                "Enter the id of the node you want to find: ",
                this::trySelectNode
            );
        });
        m.add(byId);
        
        JMenuItem byLabel = new JMenuItem("...by label");
        byLabel.addActionListener((e) -> {
            InputConsole.getInstance().askString(
                "Enter a label of the node you want to find: ", 
                this::trySelectNode
            ); 
        });
        m.add(byLabel);
        
        return m;
    }

    private JMenu createOptionMenu() {
        JMenu m = new JMenu("Options");

        JMenuItem chooseNodeSize = new JMenuItem("Change node icon size");
        chooseNodeSize.addActionListener((e) -> {
            InputConsole.getInstance().askInt(
                "Enter new size for node icons:", 
                NodeIcon::setSize
            );
        });
        m.add(chooseNodeSize);
        
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
