package nodemanager.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import nodemanager.node.Node;

public class NodeDataPane extends JComponent{
    private Node selectedNode;
    private boolean hasNodeSelected;
    private final JTextArea nodeInfo;
    public final JButton changeCoords;
    
    public NodeDataPane(){
        setLayout(new GridLayout(2, 1));
        selectedNode = null;
        hasNodeSelected = false;
        
        nodeInfo = new JTextArea("No node selected");
        nodeInfo.setBackground(Color.red);
        nodeInfo.setEditable(false);
        
        changeCoords = new JButton("Change this node's coordinates");
        changeCoords.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                if(hasNodeSelected){
                    
                }
            }
        });
        
        add(nodeInfo);
        add(changeCoords);
        setVisible(true);
    }
    
    public NodeDataPane(Node n){
        this();
        selectNode(n);
    }
    
    public void addChangeCoord(AbstractAction a){
        changeCoords.addActionListener(a);
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
