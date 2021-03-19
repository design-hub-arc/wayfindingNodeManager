package nodemanager.gui;

import java.awt.Color;
import java.awt.GridLayout;
import nodemanager.gui.editPage.mapComponents.NodeIcon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import nodemanager.NodeManager;
import nodemanager.gui.exportData.ExportMenu;
import nodemanager.gui.importData.ImportMenu;
import nodemanager.model.Graph;
import nodemanager.model.Node;
import nodemanager.modes.ModeNewNode;

/**
 *
 * @author Matt
 */
public class ApplicationMenuBar extends JMenuBar {
    private final NodeManagerWindow parent;
    
    public ApplicationMenuBar(NodeManagerWindow parent){
        super();
        this.parent = parent;
        add(addHoverBehavior(createHomeButton()));
        add(addHoverBehavior(new ImportMenu(this)));
        add(addHoverBehavior(new ExportMenu(this)));
        add(addHoverBehavior(createSelectMenu()));
        add(addHoverBehavior(createOptionMenu()));
        
        JMenuItem addNodeButton = new JMenuItem("Add a new Node");
        addNodeButton.addActionListener((ActionEvent e) -> {
            NodeManager.getInstance().setMode(new ModeNewNode());
        });
        add(addHoverBehavior(addNodeButton));
        
        JMenuItem resetData = new JMenuItem("Clear all data");
        resetData.addActionListener((ActionEvent e) -> {
            resetData();
        });
        add(addHoverBehavior(resetData));
        
        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener((ActionEvent e) -> {
            NodeManager.getInstance().getLog().undo();
        });
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        add(addHoverBehavior(undo));
        
        JMenuItem redo = new JMenuItem("Redo");
        redo.addActionListener((ActionEvent e) -> {
            NodeManager.getInstance().getLog().redo();
        });
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        add(addHoverBehavior(redo));
        
        /*
        JMenuItem test = new JMenuItem("Test");
        test.addActionListener((e)->{
            System.out.println(NodeManager.getInstance().getGraph());
        });
        add(addHoverBehavior(test));
        */
        
        setLayout(new GridLayout(1, this.getComponentCount()));
        resetData();
    }
    
    public final NodeManagerWindow getNodeManagerWindow(){
        return parent;
    }
    
    private JMenuItem createHomeButton(){
        JMenuItem ret = new JMenuItem("Home");
        ret.addActionListener((e)->{
            this.getNodeManagerWindow().getBody().switchToPage(ApplicationBody.EDIT);
        });
        return ret;
    }
    
    private JMenuItem addHoverBehavior(JMenuItem jmi){
        jmi.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                jmi.setForeground(Color.blue);
                jmi.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                jmi.setForeground(Color.black);
                jmi.repaint();
            }
        });
        return jmi;
    }
    
    private void resetData(){
        Graph g = Graph.createDefault();
        NodeManager.getInstance().setGraph(g);
        if(NodeManager.getInstance().getMap() != null){
            NodeManager.getInstance().getMap().renderGraph(g);
        }
        NodeManager.getInstance().getLog().clear();
    }
    
    private void trySelectNode(int id){
        Graph g = NodeManager.getInstance().getGraph();
        Node find = null;
        if(g == null){
            InputConsole.getInstance().warn("Can't find nodes, as no graph has been imported");
        } else {
            find = g.getNodeById(id);
        }
        if(find == null){
            InputConsole.getInstance().warn("Couldn't find a node with an id of " + id);
        } else {
            NodeManager.getInstance().getNodeDataPane().selectNode(find);
        }
    }
    
    private void trySelectNode(String label){
        Graph g = NodeManager.getInstance().getGraph();
        Node find = null;
        if(g == null){
            InputConsole.getInstance().warn("Can't find nodes, as no graph has been imported");
        } else {
            find = g.getNodeByLabel(label);
        }
        if(find == null){
            InputConsole.getInstance().warn("Couldn't find a node with a label of " + label);
        } else {
            NodeManager.getInstance().getNodeDataPane().selectNode(find);
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
            NodeManager.getInstance().getMap().setDrawAllConnections(true);
            parent.getBody().repaint();
        });
        m.add(showAllConn);
        
        JMenuItem hideAllConn = new JMenuItem("Hide all connections");
        hideAllConn.addActionListener((e) -> {
            NodeManager.getInstance().getMap().setDrawAllConnections(false);
            parent.getBody().repaint();
        });
        m.add(hideAllConn);

        return m;
    }
}
