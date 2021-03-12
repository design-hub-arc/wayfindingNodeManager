package nodemanager.node;

import java.util.*;
import nodemanager.gui.mapComponents.NodeIcon;


/**
 * The Node class is used to store data pertaining to points on campus.
 * Each Node has a unique ID, 
 * x and y coordinates on both a "source plane" and a target Container element, 
 * and the IDs of Nodes connecting to it, known as "adjacent nodes"
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class Node {
    public final int id;
    
    /*
    position on a "source plain", which basically means this point can be 
    ANY set of horizontal and vertical components on
    ANY two dimensional grid, so long as x grows right, and y grows down
    */
    private int rawX;
    private int rawY;
    
    private HashSet<Integer> adjacentIds;
    private ArrayList<String> labels; //rooms, buildings, etc.
    private NodeIcon icon;
    
    private static HashMap<Integer, Node> allNodes = new HashMap<>();
    private static HashMap<String, Node> labelToNode = new HashMap<>(); //stores as uppercase label
    
    private static int nextId = 0;
    
    /**
     * Creates a "protoNode",
     * which means all of its
     * data hasn't yet been imported
     * @param id the node's id
     */
    public Node(int id){
        this.id = id;
        rawX = 0;
        rawY = 0;
        adjacentIds = new HashSet<>();
        labels = new ArrayList<>();
        icon = new NodeIcon(this);
        
        if(id >= nextId){
            nextId = id + 1;
        }
    }
    
    public Node(int id, int x, int y){
        this(id);
        rawX = x;
        rawY = y;
        getIcon().setPos(x, y);
    }
    
    public Node(){
        this(nextId);
    }
    
    
    /**
     * Use this in lieu of a constructor to avoid creating
     * an excessive amount of nodes.
     * If a node with the given ID doesn't exist, creates one.
     * Updates the Node with the given x and y coordinates
     * @param id the id of the node to update, or the node to create
     * @param x the x coordinate of the node
     * @param y y coordinate
     * @return the node updated or created
     */
    /*
    public static Node updateNode(int id, int x, int y){
        Node ret = null;
        if(allNodes.containsKey(id)){
            ret = allNodes.get(id);
        }
        if(ret == null){
            ret = new Node(id);
        }
        ret.rawX = x;
        ret.rawY = y;
        ret.getIcon().setPos(x, y);
        
        return ret;
    }*/
    
    
    
    /**
     * Adds a node to allNodes.
     * The constructor does this automatically, 
     * but this needs to be called to undo a NodeDeleteEvent
     * @param n the node to add. If a node with that ID already exists, erases the existing node
     */
    /*
    public static void addNode(Node n){
        if(allNodes.containsKey(n.id)){
            //Update the node
            Node old = allNodes.get(n.id);
            updateNode(old.id, n.getX(), n.getY());
            n.getAdjIds().forEach((adjId)->old.addAdjId(adjId));
            for(String label : n.getLabels()){
                old.addLabel(label);
            }
            old.getIcon().nodePosUpdated();
        } else {
            allNodes.put(n.id, n);
        }
    }*/
    
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
        labelToNode.clear();
        nextId = 0;
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
     * Gets a node with the given label, if one exists
     * @param label the label to search for, ignoring case
     * @return the node with the label, or null if none exists
     * @throws NullPointerException if it returns null
     */
    public static Node get(String label) throws NullPointerException{
        return labelToNode.get(label.toUpperCase());
    }
    
    /**
     * Get all Nodes
     * @return a Collection of all Nodes
     */
    public static Collection<Node> getAll(){
        return allNodes.values();
    }
    
    public final int getId(){
        return id;
    }
    
    //todo: don't changes original x
    public void setX(int x){
        this.rawX = x;
    }
    public void setY(int y){
        this.rawY = y;
    }
    
    /**
     * the x coordinate of this Node on the source plane
     * @return the x coordinate of this Node on the source plane
     */
    public int getX(){
        return rawX;
    }
    
    /**
     * the y coordinate of this Node on the source plane
     * @return the y coordinate of this Node on the source plane
     */
    public int getY(){
        return rawY;
    }
    
    /**
     * Creates a connection between this Node and another.
     * If a node with that id doesn't exist yet,
     * creates a protoNode to hold the data
     * @param i the ID of the Node to connect to
     */
    public void addAdjId(int i){
        if(Node.get(i) == null){
            //addNode(new Node(i));
        }
        if(!isAdjTo(i)){
           adjacentIds.add(i);
        }
        if(!Node.get(i).isAdjTo(id)){
            Node.get(i).addAdjId(id);
        }
    }
    
    /**
     * Adds a label to this node
     * @param s the label to add (room number, building name, etc)
     * @return whether or not the label was successfully added
     */
    public boolean addLabel(String s){
        boolean ret = !labelToNode.containsKey(s.toUpperCase());
        if(ret){
            labelToNode.put(s.toUpperCase(), this);
            labels.add(s);
        }
        
        return ret;
    }
    
    
    /**
     * Removes a label from this,
     * if it has the given label,
     * ignoring case
     * @param s the label to remove
     * @return whether or not a label was removed 
     */
    public boolean removeLabel(String s){
        boolean found = false;
        for(int i = 0; i < labels.size() && !found; i++){
            if(labels.get(i).equalsIgnoreCase(s)){
                found = true;
                labels.remove(i);
                labelToNode.remove(s.toUpperCase());
            }
        }
        return found;
    }
    
    /**
     * Severs a connection between this Node and another. Does nothing if no connection exists
     * @param i the ID of the Node to disconnect from
     * @return whether or not an adj was removed
     */
    public boolean removeAdj(int i){
        Integer remId = i;
        boolean ret = adjacentIds.contains(remId);
        if(ret){
            Node connected = Node.get(i);
            adjacentIds.remove(remId);
            if(connected.adjacentIds.contains(id)){
                connected.removeAdj(id);
            }
        }
        return ret;
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
    
    public String[] getLabels(){
        return Arrays.copyOf(labels.toArray(), labels.size(), String[].class);
    }
    
    /**
     * checks if this node has the given label,
     * ignoring case.
     * 
     * @param label the label to search for
     * @return if this has the given label, ignoring case
     */
    public boolean getHasLabel(String label) {
        return labels.stream().anyMatch(l -> l.equalsIgnoreCase(label));
    }
    
    /**
     * Returns the visual representation of this Node.
     * @return the NodeIcon generated by this node
     */
    public NodeIcon getIcon(){
        return icon;
    }
    
    /**
     * Updates this' position to match that of its icon
     */
    public void update(){
        rawX = (int)icon.getScale().inverseX(icon.getX());
        rawY = (int)icon.getScale().inverseY(icon.getY());
    }
    
    @Override
    public String toString(){
        return String.format("Node#%d: (%d, %d)", id, rawX, rawY);
    }
}
