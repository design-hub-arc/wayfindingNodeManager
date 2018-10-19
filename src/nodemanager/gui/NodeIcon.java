package nodemanager.gui;

import javax.swing.JLabel;
import javax.swing.JToolTip;
import java.awt.*;
import java.awt.event.*;
import nodemanager.node.Node;

public class NodeIcon extends JLabel implements MouseListener{
    private final Node node;
    private Scale scale;
    
    public NodeIcon(Node n){
        super(Integer.toString(n.id));
        node = n;
        setSize(30, 30);
        setBackground(Color.red);
        setOpaque(true);
        setVisible(true);
        addMouseListener(this);
    }
    
    public void scaleTo(Scale s){
        scale = s;
        this.setLocation(s.x(node.rawX), s.y(node.rawY));
    }
    
    private void drawLink(Node n){
        Graphics2D g = (Graphics2D)getParent().getGraphics();
        g.setColor(Color.red);
        g.setStroke(new BasicStroke(10));
        g.drawLine(getX(), getY(), scale.x(n.rawX), scale.y(n.rawY));
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        node.displayData();
        // select this node
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {
        node.getAdj().stream().forEach(n -> drawLink(n));
        this.setToolTipText(node.getDesc());
    }
    
    //flickering
    @Override
    public void mouseExited(MouseEvent me) {
        getParent().repaint();
    }
    /*
    public JToolTip createToolTip(){
        JToolTip ret = new JToolTip();
        
        return ret;
    }*/
}
