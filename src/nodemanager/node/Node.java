package nodemanager.node;

import java.io.*;
import java.util.*;
import nodemanager.gui.NodeIcon;
import static java.lang.System.out;
import java.text.SimpleDateFormat;

//seperate this into Node and NodeListener or something like that

/**
 * The Node class is used to store data pertaining to points on campus.
 * Each Node has a unique ID, 
 * x and y coordinates on both a "source plane" and a target Container element, 
 * and the IDs of Nodes connecting to it, known as "adjacent nodes"
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class Node{
    public final int id;
    
    /*
    position on a "source plain", which basically means this point can be 
    ANY set of horizontal and vertical components on
    ANY two dimensional grid, so long as x grows right, and y grows down
    */
    public final int rawX;
    public final int rawY;
    
    private HashSet<Integer> adjacentIds;
    private NodeIcon icon;
    
    private static HashMap<Integer, Node> allNodes = new HashMap<>();
    private static int nextId = 0;
    
    /**
     * 
     * @param id the id of this Node
     * @param x the horizontal component of this Node's position on its source plane
     * @param y the vertical component of this Node's position on its source plane
     */
    public Node(int id, int x, int y){
        this.id = id;
        rawX = x;
        rawY = y;
        
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
    public Node(int x, int y){
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
