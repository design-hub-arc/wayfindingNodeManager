package nodemanager.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import nodemanager.Mode;
import nodemanager.Session;
import nodemanager.events.*;
import nodemanager.io.InputConsole;
import nodemanager.node.Node;

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
                Session.logAction(new NodeDeleteEvent(selectedNode, selectedNode.getIcon().getHost()));
                selectedNode.getIcon().getHost().removeNode(selectedNode);
                Node.removeNode(selectedNode.id);
                Session.selectNode(Node.get(-1));
            }
        });
        
        addOption("Move this node", () -> {
            if(selectedNode.id < 0){
                ip.warn("Cannot move node with id of " + selectedNode.id);
            } else {
                Session.setMode(Mode.MOVE);
                Session.logAction(new NodeMovedEvent(selectedNode, selectedNode.getIcon().getX(), selectedNode.getIcon().getY()));
            }
        });
        
        //resets the NodeIcon to its position when it was initially imported
        addOption("Reset position", () -> {
            Session.logAction(new NodeMovedEvent(selectedNode, selectedNode.getIcon().getX(), selectedNode.getIcon().getY()));
            selectedNode.getIcon().resetPos();
        });
        
        addOption("Add a connection", () -> {
            Session.setMode(Mode.ADD_CONNECTION);
        });
        
        addOption("Remove a connection", () -> {
            Session.setMode(Mode.REMOVE_CONNECTION);
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
                    if(selectedNode.removeLabel(label)){
                        Session.logAction(new LabelRemovedEvent(selectedNode, label));
                        selectNode(selectedNode); //reload node description
                    }
                }
            );
        });
        
        setVisible(true);
    }
    
    private void tryAddLabel(String label){
        if(selectedNode.addLabel(label)){
            Session.logAction(new LabelAddedEvent(selectedNode, label));
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
        if(hasNodeSelected){
            selectedNode.getIcon().setDrawLinks(false);
        }
        hasNodeSelected = true;
        selectedNode = n;
        nodeInfo.setText(n.getDesc());
        
        SwingUtilities.invokeLater(()->{
            infoView.getVerticalScrollBar().setValue(0);
        });
        
        n.getIcon().setDrawLinks(true);
        n.getIcon().getHost().repaint();
    }
    
    /**
     * returns the Node this is displaying the data for
     * @return the selected Node
     */
    public Node getSelected(){
        return selectedNode;
    }
}
