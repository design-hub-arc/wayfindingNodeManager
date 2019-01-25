package nodemanager.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.*;
import nodemanager.gui.NodeIcon;
import static java.lang.System.out;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import nodemanager.Mode;
import nodemanager.Session;
import nodemanager.gui.Scale;

//seperate this into Node and NodeListener or something like that

/**
 * The Node class is used to store data pertaining to points on campus.
 * Each Node has a unique ID, 
 * x and y coordinates on both a "source plane" and a target Container element, 
 * and the IDs of Nodes connecting to it, known as "adjacent nodes"
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class Node implements MouseListener{
    public final int id;
    
    /*
    position on a "source plain", which basically means this point can be 
    ANY set of horizontal and vertical components on
    ANY two dimensional grid, so long as x grows right, and y grows down
    */
    public final double rawX;
    public final double rawY;
    
    //coordinates on destination plane
    private int x;
    private int y;
    
    private HashSet<Integer> adjacentIds;
    private NodeIcon icon;
    
    private static HashMap<Integer, Node> allNodes = new HashMap<>();
    private static int nextId = 0;
    
    private Color color;
    private static int size = 30; //add ability to resize
    
    /**
     * 
     * @param id the id of this Node
     * @param x the horizontal component of this Node's position on its source plane
     * @param y the vertical component of this Node's position on its source plane
     */
    public Node(int id, double x, double y){
        this.id = id;
        rawX = x;
        rawY = y;
        this.x = (int)x;
        this.y = (int)y;
        
        color = (id > 0) ? Color.blue : Color.red;
        
        adjacentIds = new HashSet<>();
        icon = new NodeIcon(this);
        
        //need this for now, but can remove once we get to one line per node in node file
        if(!allNodes.containsKey(id)){
            allNodes.put(id, this);
            if(id >= nextId){
                nextId = id + 1;
            }
        }
    }
    
    /**
     * Generates a new Node at the given coordinates, with a unique ID.
     * @param x the horizontal component of this Node's position on its source plane
     * @param y the vertical component of this Node's position on its source plane
     */
    public Node(double x, double y){
        this(nextId, x, y);
    }
    
    /**
     * Removes a Node from the program, and severs any connections between this Node and those adjacent to it.
     * Note that it doesn't modify the original spreadsheet
     * @param id the ID of the Node to remove
     */
    public static void removeNode(int id){
        
        Node n = get(id);
        if(n != null){
            n.adjacentIds.stream().forEach(i -> get(i).adjacentIds.remove(Integer.valueOf(id)));
        }
        allNodes.remove(id);
    }
    
    /**
     * Clears the program's Node list.
     * Doesn't modify the spreadsheet
     */
    public static void removeAll(){
        allNodes.clear();
    }
    
    //how deal with not found?
    /**
     * Returns a Node with the given ID
     * @param nodeId the ID to get a Node with
     * @return the Node with the given ID, or null if one doesn't exist
     * @throws NullPointerException 
     */
    public static Node get(int nodeId) throws NullPointerException{
        return allNodes.get(nodeId);
    }
    
    /**
     * Get all Nodes
     * @return a Collection of all Nodes
     */
    public static Collection<Node> getAll(){
        return allNodes.values();
    }
    
    /**
     * 
     * @return the size of nodes, in pixels
     */
    public static int getSize(){
        return size;
    }
    
    /**
     * Sets this Node's destination plane,
     * setting its coordinates.
     * @param s the Scale to position to.
     */
    public void scaleTo(Scale s){
        x = s.x(rawX);
        y = s.y(rawY);
    }
    
    /**
     * Repositions this Node on its destination plane,
     * such as the MapImage
     * @param x the x coordinate to set to
     * @param y the y coordinate to set to
     */
    public void repos(double x, double y){
        this.x = (int)x;
        this.y = (int)y;
    }
    
    /**
     * Resets this Node's position on the destination plane 
     * to where it when it was initially imported
     */
    public void resetPos(){
        x = (int)rawX;
        y = (int)rawY;
    }
    
    /**
     * the x coordinate of this Node on the destination plane
     * @return the x coordinate of this Node on the destination plane
     */
    public int getX(){
        return x;
    }
    
    /**
     * the y coordinate of this Node on the destination plane
     * @return the y coordinate of this Node on the destination plane
     */
    public int getY(){
        return y;
    }
    
    /**
     * Creates a connection between this Node and another
     * @param i the ID of the Node to connect to
     */
    public void addAdjId(int i){
        if(Node.get(i) == null){
            out.println(String.format("Node with ID %d does not exist.", i));
        } else {
            if(!isAdjTo(i)){
               adjacentIds.add(i);
            }
            if(!Node.get(i).isAdjTo(id)){
                Node.get(i).addAdjId(id);
            }
        }
    }
    
    /**
     * Severs a connection between this Node and another. Does nothing if no connection exists
     * @param i the ID of the Node to disconnect from
     */
    public void removeAdj(int i){
        Integer remId = i;
        if(adjacentIds.contains(remId)){
            Node connected = Node.get(i);
            adjacentIds.remove(remId);
            if(connected.adjacentIds.contains(remId)){
                connected.removeAdj(id);
            }
        }
    }
    
    /**
     * Checks to see if this Node connects to one with a given ID
     * @param i the ID of the Node to check for a connection with
     * @return whether or not the two Nodes connect
     */
    public boolean isAdjTo(int i){
        return adjacentIds.contains(i);
    }
    
    /**
     * gives all adjacent Node ids
     * @return a HashSet of all adjacent Node ids
     */
    public HashSet<Integer> getAdjIds(){
        return adjacentIds;
    }
    
    /**
     * Returns the visual representation of this Node.
     * @return the NodeIcon generated by this node
     */
    public NodeIcon getIcon(){
        return icon;
    }
    
    /**
     * Get the textual description of this Node
     * @return a description of this Node
     */
    public String getDesc(){
        String ret = 
                "Node #" + id + System.lineSeparator() +
                "Raw coordinates: (" + (int)rawX + ", " + (int)rawY + ")" + System.lineSeparator() +
                "Adjacent ids: " + System.lineSeparator();
        //streaming doesn't work here
        for(int i : adjacentIds){
            ret += ("* " + i + System.lineSeparator());
        }
        return ret;
    }
    
    /**
     * Prints this Node's data to the console
     */
    public void displayData(){
        out.println(getDesc());
    }
    
    /**
     * Prints the data of all Nodes to the console
     */
    public static void logAll(){
        out.println("*ALL NODES*");
        for(Node n : allNodes.values()){
            out.println("");
            n.displayData();
        }
    }
    
    
    /**
     * Draws this node on a Graphics object
     * @param g the graphics context to draw on
     */
    public void draw(Graphics g){
        g.setColor(color);
        g.fillRect(x, y, size, size);
        g.setColor(Color.black);
        g.drawString(Integer.toString(id), x, y);
    }
    
    /**
     * Draws a link between this node and another
     * @param g the graphics context to draw on
     * @param n the node to draw a link to
     */
    public void drawLink(Graphics2D g, Node n){
        g.setColor(Color.blue);
        g.setStroke(new BasicStroke(10));
        g.drawLine(getX(), getY(), n.getX(), n.getY());
    }
    
    /**
     * Visualize the connections between 
     * this node and those adjacent to it
     * @param g the graphics context to draw on
     */
    public void drawLinks(Graphics2D g){
        getAdjIds().stream().map(id -> Node.get(id)).forEach(n -> drawLink(g, n));
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        switch (Session.mode) {
            case NONE:
                Session.selectNode(this);
                this.displayData();
                break;
            case ADD_CONNECTION:
                Session.selectedNode.addAdjId(id);
                Session.mode = Mode.NONE;
                break;
            case REMOVE_CONNECTION:
                Session.selectedNode.removeAdj(id);
                removeAdj(Session.selectedNode.id);
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
        //drawLinks();
    }
    
    //flickering
    @Override
    public void mouseExited(MouseEvent me) {}
    
    /**
     * Exports the Node data in the form of two csv files
     * @param path the path to the directory where the new files will be created
     */
    public static void generateDataAt(String path){
        BufferedWriter out = null;
        String nl = System.getProperty("line.separator");
        
        String time = new SimpleDateFormat("MM_dd_yyyy").format(Calendar.getInstance().getTime());
        
        try {
            File nodeFile = new File(path + File.separator + "nodeData" + time + ".csv");
            File connectFile = new File(path + File.separator + "nodeConnections" + time + ".csv");
            
            out = new BufferedWriter(new FileWriter(nodeFile.getAbsolutePath()));
            out.write("id, x, y" + nl);
            for(Node n : allNodes.values()){
                out.write(n.id + ", " + n.getIcon().getX() + ", " + n.getIcon().getY() + nl);
            }
            out.close();
            
            out = new BufferedWriter(new FileWriter(connectFile.getAbsoluteFile()));
            for(Node n : allNodes.values()){
                for(int id : n.adjacentIds){
                    //TODO: no duplicates
                    out.write(n.id + ", " + id + nl);
                }
            }
            out.close();
        } catch (FileNotFoundException ex) {
            
        } catch (IOException ex) {
            
        }
    }
}
