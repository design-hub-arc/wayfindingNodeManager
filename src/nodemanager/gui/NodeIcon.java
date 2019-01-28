package nodemanager.gui;

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
 * NodeIcons are used to visually display Nodes on a MapImage, 
 * as well as respond to mouse clicks.
 * 
 * Note how this does not implement a mouselistener. 
 * That is because this would have to be a component to do so,
 * but since NodeIcons need to be constantly repositioned,
 * java doesn't like that.
 * Solving the problem: MapImage will pass click events to this' mouse event listeners.
 */
public class NodeIcon{
    public final Node node;
    private final int id; //the ID to display
    private final Color color;
    private Scale scale;
    private int x; //position on the map image
    private int y;
    private boolean drawLinks; //whether or not this should draw links
    
    private static int size = 30; //add ability to resize
    
    /**
     * Creates a visual representation of the given Node
     * @param n the Node to create a representation of
     */
    public NodeIcon(Node n){
        id = n.id;
        node = n;
        color = (n.id < 0) ? Color.green : Color.red;
        
        x = node.rawX;
        y = node.rawY;
        
        drawLinks = false;
    }
    
    /**
     * Gets the width and height of NodeIcons, in pixels
     * @return the size of NodeIcons
     */
    public static int getSize(){
        return size;
    }
    
    
    /**
     * Gets the Node this represents
     * @return the Node this represents.
     */
    public Node getNode(){
        return node;
    }
    
    
    /**
     * 
     * @return this' x-coordinate on the map image
     */
    public int getX(){
        return x;
    }
    
    /**
     * 
     * @return this' y-coordinate on the map image
     */
    public int getY(){
        return y;
    }
    
    /**
     * Sets the position of this icon on the map image.
     * Note that this does not edit this' node
     * @param xc the x-coordinate on the map
     * @param yc the y-coordinate on the map
     */
    public void setPos(int xc, int yc){
        x = xc;
        y = yc;
    }
    
    /**
     * Sets the scale this should be positioned based on,
     * @see Scale for more info
     * @param s the Scale to set position off of
     */
    public void scaleTo(Scale s){
        scale = s;
        x = scale.x(node.getX());
        y = scale.y(node.getY());
    }
    
    /**
     * Moves this component back to where it was when the Node was initially imported
     */
    public void resetPos(){
        x = scale.x(node.rawX);
        y = scale.y(node.rawY);
    }
    
    
    /**
     * Checks to see if the given points 
     * is contained within this NodeIcon
     * @param xc the x-coordinate of the click, as a point on the map image
     * @param yc the y-coordinate of the click, as a point on the map image
     * @return whether or not this was clicked on
     */
    public boolean isIn(int xc, int yc){
        return x <= xc && 
               x + size >= xc &&
               y <= yc &&
               y + size >= yc;
    }
    
    
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

    public void mouseEntered(MouseEvent me) {
        drawLinks = true;
        //make this change to being displayed as node data
    }
    
    public void mouseExited(MouseEvent me) {
        drawLinks = false;
        //make revert to displayed as node icon
    }
    
    
    /**
     * Draws this on a Graphics object
     * @param g the graphics context to draw on
     */
    public void draw(Graphics g){
        g.setColor(color);
        g.fillRect(x, y, size, size);
        
        g.setColor(Color.black);
        g.drawString(Integer.toString(id), x, y);
        
        if(drawLinks){
            drawAllLinks(g);
        }
    }
    
    
    /**
     * Graphically display all the connections between this icon's Nodes and its adjacent Nodes
     * @param g the Graphics context to draw on
     */
    private void drawAllLinks(Graphics g){
        node.getAdjIds()
                .stream()
                .map(id -> Node.get(id))
                .forEach(node -> drawLink(g, node));
    }
    
    
    /**
     * Draw a link between this NodeIcon and another Node's icon
     * @param n the Node to draw a connection to
     */
    private void drawLink(Graphics g, Node n){
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawLine(getX(), getY(), n.getIcon().getX(), n.getIcon().getY());
    }
}
