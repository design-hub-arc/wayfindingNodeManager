package nodemanager.gui;

import nodemanager.gui.editPage.mapComponents.NodeIcon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import nodemanager.NodeManager;
import nodemanager.Session;
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
        add(createHomeButton());
        add(new ImportMenu(this));
        add(new ExportMenu(this));
        add(createSelectMenu());
        add(createOptionMenu());
        
        JMenuItem addNodeButton = new JMenuItem("Add a new Node");
        addNodeButton.addActionListener((ActionEvent e) -> {
            NodeManager.getInstance().setMode(new ModeNewNode());
        });
        add(addNodeButton);
        
        JMenuItem resetData = new JMenuItem("Clear all data");
        resetData.addActionListener((ActionEvent e) -> {
            resetData();
        });
        add(resetData);
        
        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener((ActionEvent e) -> {
            NodeManager.getInstance().getLog().undo();
        });
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        add(undo);
        
        JMenuItem redo = new JMenuItem("Redo");
        redo.addActionListener((ActionEvent e) -> {
            NodeManager.getInstance().getLog().redo();
        });
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        add(redo);
        
        JMenuItem test = new JMenuItem("Test");
        test.addActionListener((e)->{
            System.out.println(NodeManager.getInstance().getGraph());
        });
        add(test);
        
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
    private void resetData(){
        Graph g = Graph.createDefault();
        NodeManager.getInstance().setGraph(g);
        if(Session.map != null){
            Session.map.renderGraph(g);
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
            Graph g = NodeManager.getInstance().getGraph();
            if(g != null){
                g.getAllNodes().forEach(node -> {
                    Session.map.getIcon(node.getId()).setDrawLinks(true);
                });
                parent.getBody().repaint();
            }
        });
        m.add(showAllConn);
        
        JMenuItem hideAllConn = new JMenuItem("Hide all connections");
        hideAllConn.addActionListener((e) -> {
            Graph g = NodeManager.getInstance().getGraph();
            if(g != null){
                g.getAllNodes().forEach(node -> {
                    Session.map.getIcon(node.getId()).setDrawLinks(false);
                });
                parent.getBody().repaint();
            }
        });
        m.add(hideAllConn);

        return m;
    }
}
