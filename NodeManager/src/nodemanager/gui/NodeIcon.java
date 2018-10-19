package nodemanager.gui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import nodemanager.node.Node;

public class NodeIcon extends JComponent{
    private Node node;
    
    public NodeIcon(Node n){
        super();
        node = n;
        setSize(50, 50);
    }
    
    public void scaleTo(Scale s){
        this.setLocation(s.x(node.rawX), s.y(node.rawY));
    }
    
    //wait a sec... is this drawing from 
    //only drawing at upper left corner of map, but getX and y work
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        System.out.println("Drawing node icon @ " + getX() + ", " + getY() +" with a size of " + getWidth() + "x" + getHeight());
        g.setColor(Color.red);
        g.fillRect(getX(), getY(), getWidth(), getHeight());
    }
}
