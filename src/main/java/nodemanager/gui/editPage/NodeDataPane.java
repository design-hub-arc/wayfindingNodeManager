package nodemanager.gui.editPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import nodemanager.NodeManager;
import nodemanager.Session;
import nodemanager.events.*;
import nodemanager.gui.InputConsole;
import nodemanager.gui.editPage.mapComponents.NodeIcon;
import nodemanager.model.Graph;
import nodemanager.model.Node;
import nodemanager.modes.ModeAddConnection;
import nodemanager.modes.ModeMove;
import nodemanager.modes.ModeRemoveConnection;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */

 /** 
 * NodeDataPane is used to display information on the currently selected Node 
 */
public class NodeDataPane extends JComponent{
    private Node selectedNode;
    private boolean hasNodeSelected;
    private final JTextArea nodeInfo;
    private final JScrollPane infoView;
    private GridBagConstraints gbc;
    
    private static final String NONE_SELECTED_MSG = "Click on a node to select it";
    
    public NodeDataPane(){
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        selectedNode = null;
        hasNodeSelected = false;
        
        nodeInfo = new JTextArea(NONE_SELECTED_MSG);
        nodeInfo.setColumns(NONE_SELECTED_MSG.length());
        nodeInfo.setRows(5);
        nodeInfo.setBackground(Color.white);
        nodeInfo.setEditable(false);
        nodeInfo.setLineWrap(true);
        infoView = new JScrollPane(
            nodeInfo,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        );
        add(infoView, gbc);
        
        InputConsole ip = InputConsole.getInstance();
        
        addOption("Delete this node", () ->{
            if(selectedNode.id < 0){
                //prevent user from deleting corner nodes
                ip.warn("Cannot delete node with id of " + selectedNode.id);
            } else {
                Graph g = NodeManager.getInstance().getGraph();
                NodeManager.getInstance().getLog().log(new NodeDeleteEvent(g, selectedNode, Session.map));
                Session.map.removeNode(selectedNode);
                g.removeNode(selectedNode.id);
                selectNode(g.getNodeById(-1));
            }
        });
        
        addOption("Move this node", () -> {
            if(selectedNode.id < 0){
                ip.warn("Cannot move node with id of " + selectedNode.id);
            } else {
                Graph g = NodeManager.getInstance().getGraph();
                NodeManager.getInstance().setMode(new ModeMove(selectedNode));
                NodeIcon icon = Session.map.getIcon(selectedNode.id);
                NodeManager.getInstance().getLog().log(new NodeMovedEvent(g, selectedNode, icon.getX(), icon.getY()));
            }
        });
        
        addOption("Add a connection", () -> {
            if(this.selectedNode.getId() >= 0){
                NodeManager.getInstance().setMode(new ModeAddConnection(this.selectedNode.getId()));
            } else {
                ip.warn(String.format("Cannot add connections to node %d", selectedNode.getId()));
            }
        });
        
        addOption("Remove a connection", () -> {
            if(this.selectedNode.getId() >= 0){
                NodeManager.getInstance().setMode(new ModeRemoveConnection(this.selectedNode.getId()));
            } else {
                ip.warn(String.format("Cannot remove connections from node %d", selectedNode.getId()));
            }
        });
        
        addOption("Add a label", () -> {
            ip.askString(
                "Enter the label to add to this node: ", 
                this::tryAddLabel
            );
        });
        
        addOption("Add a URL", ()->{
            ip.askString("Enter the URL to add: ", this::tryAddLabel);
            ip.askString("What do you want to call this URL?", this::tryAddLabel);
        });
        
        addOption("Remove a label", ()->{
            ip.askString(
                "Enter the label to remove from this node: ",
                (String label)->{
                    Graph g = NodeManager.getInstance().getGraph();
                    Node labeled = g.getNodeByLabel(label);
                    if(g.removeLabel(label)){
                        NodeManager.getInstance().getLog().log(new LabelRemovedEvent(g, labeled, label));
                        selectNode(selectedNode); //reload node description
                    }
                }
            );
        });
        
        setVisible(true);
    }
    
    private void tryAddLabel(String label){
        Graph g = NodeManager.getInstance().getGraph();
        if(g.addLabel(label, selectedNode.getId())){
            NodeManager.getInstance().getLog().log(new LabelAddedEvent(g, selectedNode, label));
            selectNode(selectedNode); //reload node description
        } else {
            InputConsole.getInstance().warn(String.format("Label '%s' is already in use.", label));
        }
    }
    
    /**
     * Adds a button to the data pane
     * @param title what the button will display
     * @param action the runnable to run when the button is clicked, if a node is selected, 
     * otherwise it does nothing
     */
    private void addOption(String title, Runnable action){
        JButton j = new JButton(title);
        j.addActionListener((ActionEvent e) -> {
            if(hasNodeSelected){
                action.run();
            }
        });
        add(j, gbc);
    }
    
    
    /**
     * Creates a NodeDataPane with a specific node selected
     * @param n the Node to select
     */
    public NodeDataPane(Node n){
        this();
        selectNode(n);
    }
    
    /**
     * Changes the information displayed to that of a given node
     * @param n the Node this should display the data for
     */
    public void selectNode(Node n){
        NodeIcon icon = Session.map.getIcon(n.getId());
        if(hasNodeSelected){
            Session.map.getIcon(selectedNode.getId()).setDrawLinks(false);
        }
        hasNodeSelected = true;
        selectedNode = n;
        
        Graph dataSet = NodeManager.getInstance().getGraph();
        if(dataSet != null){
            nodeInfo.setText(dataSet.getDescriptionForNode(n.getId()));
        }
        
        SwingUtilities.invokeLater(()->{
            infoView.getVerticalScrollBar().setValue(0);
        });
        
        icon.setDrawLinks(true);
        Session.map.repaint();
    }
    
    /**
     * returns the Node this is displaying the data for
     * @return the selected Node
     */
    public Node getSelected(){
        return selectedNode;
    }
}
