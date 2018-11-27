package nodemanager.gui;

import javax.swing.JLabel;
import javax.swing.JToolTip;
import java.awt.*;
import java.awt.event.*;
import nodemanager.Mode;
import nodemanager.Session;
import nodemanager.node.Node;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */



/**
 * NodeIcons are used to visually display Nodes on a MapImage, as well as respond to mouse clicks.
 * We will likely replace NodeIcon in later versions, as panning the map image requires us to move ALL the NodeIcons on them map,
 * which is somewhat tedious. Also, Components are meant to be in a layout, which means they aren't meant to be constantly repositioned.
 * 
 * In spite of all that, it's just easier this way. Will deprecate later.
 */
public class NodeIcon extends JLabel implements MouseListener{
    public final Node node;
    private Scale scale;
    
    /**
     * Creates a visual representation of the given Node
     * @param n the Node to create a representation of
     */
    public NodeIcon(Node n){
        super(Integer.toString(n.id));
        node = n;
        setSize(30, 30);
        setBackground((n.id < 0) ? Color.green : Color.red);
        setOpaque(true);
        setVisible(true);
        addMouseListener(this);
    }
    
    /**
     * Sets the scale this should be positioned based on,
     * @see Scale for more info
     * @param s the Scale to set position off of
     */
    public void scaleTo(Scale s){
        scale = s;
        setLocation(scale.x(node.getX()), scale.y(node.getY()));
    }
    
    /**
     * Moves this component back to where it was when the Node was initially imported
     */
    public void resetPos(){
        setLocation(scale.x(node.rawX), scale.y(node.rawY));
        node.resetPos();
    }
    
    /**
     * Graphically display all the connections between this icon's Nodes and its adjacent Nodes
     */
    public void drawAllLinks(){
        node.getAdjIds().stream().map(id -> Node.get(id)).forEach(n -> drawLink(n));
    }
    
    /**
     * Draw a link between this NodeIcon and another Node's icon
     * @param n the Node to draw a connection to
     */
    private void drawLink(Node n){
        Graphics2D g = (Graphics2D)getParent().getGraphics();
        g.setColor(Color.red);
        g.setStroke(new BasicStroke(10));
        g.drawLine(getX(), getY(), n.getIcon().getX(), n.getIcon().getY());
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        switch (Session.mode) {
            case NONE:
                Session.selectNode(node);
                node.displayData();
                break;
            case ADD_CONNECTION:
                Session.selectedNode.addAdjId(node.id);
                Session.mode = Mode.NONE;
                break;
            case REMOVE_CONNECTION:
                Session.selectedNode.removeAdj(node.id);
                node.removeAdj(Session.selectedNode.id);
                Session.mode = Mode.NONE;
                break;
            default:
                break;
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
    
    /**
     * In the future, I kinda want to display the node's data on the tooltip,
     * but we'll need some way to do multiple lines of text.
     * 
     * @return the tooltip for this component
     */
    @Override
    public JToolTip createToolTip(){
        JToolTip ret = new JToolTip();
        
        return ret;
    }
}
