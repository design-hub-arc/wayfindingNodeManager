package nodemanager.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import nodemanager.Mode;
import nodemanager.Session;
import nodemanager.node.Node;

public class NodeDataPane extends JComponent{
    private Node selectedNode;
    private boolean hasNodeSelected;
    private final JTextArea nodeInfo;
    public final JButton delete;
    public final JButton move;
    
    public NodeDataPane(){
        setLayout(new GridLayout(3, 1));
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
        
        setVisible(true);
    }
    
    public NodeDataPane(Node n){
        this();
        selectNode(n);
    }
    
    public void selectNode(Node n){
        hasNodeSelected = true;
        selectedNode = n;
        nodeInfo.setText(n.getDesc());
    }
    
    public Node getSelected(){
        return selectedNode;
    }
}
