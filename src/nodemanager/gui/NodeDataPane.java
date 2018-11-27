package nodemanager.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import nodemanager.Mode;
import nodemanager.Session;
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
    public final JButton delete;
    public final JButton move;
    public final JButton resetPos;
    public final JButton addConn;
    public final JButton remConn;
    
    public NodeDataPane(){
        setLayout(new GridLayout(6, 1));
        selectedNode = null;
        hasNodeSelected = false;
        
        nodeInfo = new JTextArea("No node selected");
        nodeInfo.setBackground(Color.red);
        nodeInfo.setEditable(false);
        add(nodeInfo);
        
        delete = new JButton("Delete this node");
        delete.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(hasNodeSelected){
                    if(selectedNode.id < 0){
                        //prevent user from deleting corner nodes
                        JOptionPane.showMessageDialog(null, "Cannot delete node with id of " + selectedNode.id);
                    } else if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this node? This will not alter the original spreadsheet.") == JOptionPane.YES_OPTION){
                        NodeIcon n = selectedNode.getIcon();
                        Container c = n.getParent();
                        Node.removeNode(selectedNode.id);
                        c.remove(n);
                        c.revalidate();
                        c.repaint();
                        Session.selectNode(Node.get(-1));
                    }
                }
            }
        });
        add(delete);
        
        move = new JButton("Move this node");
        move.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(hasNodeSelected){
                    if(selectedNode.id < 0){
                        JOptionPane.showMessageDialog(null, "Cannot move node with id of " + selectedNode.id);
                    } else {
                        Session.mode = Mode.MOVE;
                    }
                }
            }
        });
        add(move);
        
        resetPos = new JButton("Reset position");
        
        //resets the NodeIcon to its position when it was initially imported
        resetPos.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(hasNodeSelected){
                    selectedNode.getIcon().resetPos();
                }
            }
        });
        add(resetPos);
        
        addConn = new JButton("Add a connection");
        addConn.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(hasNodeSelected){
                    JOptionPane.showMessageDialog(null, "Click on a node to connect it to node " + selectedNode.id);
                    Session.mode = Mode.ADD_CONNECTION;
                }
            }
        });
        add(addConn);
        
        remConn = new JButton("Remove a connection");
        remConn.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(hasNodeSelected){
                    JOptionPane.showMessageDialog(null, "Click on a node to disconnect it from node " + selectedNode.id);
                    Session.mode = Mode.REMOVE_CONNECTION;
                }
            }
        });
        add(remConn);
        
        setVisible(true);
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
        hasNodeSelected = true;
        selectedNode = n;
        nodeInfo.setText(n.getDesc());
    }
    
    /**
     * returns the Node this is displaying the data for
     * @return the selected Node
     */
    public Node getSelected(){
        return selectedNode;
    }
}
