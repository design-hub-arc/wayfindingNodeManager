package nodemanager.gui;

import javax.swing.JLabel;
import javax.swing.JToolTip;
import java.awt.*;
import java.awt.event.*;
import nodemanager.Mode;
import nodemanager.Session;
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
        initPos();
    }
    
    public void initPos(){
        setLocation(scale.x(node.rawX), scale.y(node.rawY));
    }
    
    public void drawAllLinks(){
        node.getAdjIds().stream().map(id -> Node.get(id)).forEach(n -> drawLink(n));
    }
    
    private void drawLink(Node n){
        Graphics2D g = (Graphics2D)getParent().getGraphics();
        g.setColor(Color.red);
        g.setStroke(new BasicStroke(10));
        g.drawLine(getX(), getY(), scale.x(n.rawX), scale.y(n.rawY));
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if(Session.mode == Mode.NONE){
            Session.selectNode(node);
            node.displayData();
        } else if(Session.mode == Mode.ADD_CONNECTION){
            Session.selectedNode.addAdjId(node.id);
            node.addAdjId(Session.selectedNode.id);
            
            Session.selectedNode.init();
            node.init();
            
            Session.mode = Mode.NONE;
        } else if(Session.mode == Mode.REMOVE_CONNECTION){
            Session.selectedNode.removeAdj(node.id);
            node.removeAdj(Session.selectedNode.id);
            Session.mode = Mode.REMOVE_CONNECTION;
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {
        drawAllLinks();
        setToolTipText(node.getDesc());
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
