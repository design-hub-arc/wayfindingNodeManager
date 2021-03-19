package nodemanager.model;

/**
 * The Node class is used to store data pertaining to points on campus.
 * Each Node has a unique ID, 
 * x and y coordinates on both a "source plane" and a target Container element, 
 * and the IDs of Nodes connecting to it, known as "adjacent nodes"
 * 
 * These adjacent nodes (edges) are stored in Graph
 * Labels are also stored in Graph
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 * @see nodemanager.model.Graph
 */
public class Node {
    public final int id;
    private int x;
    private int y;
    
    public Node(int id, int x, int y){
        this.id = id;
        this.x = x;
        this.y = y;
    } 
       
    public final int getId(){
        return id;
    }
    
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    
    /**
     * the x coordinate of this Node on the source plane
     * @return the x coordinate of this Node on the source plane
     */
    public int getX(){
        return x;
    }
        
    /**
     * the y coordinate of this Node on the source plane
     * @return the y coordinate of this Node on the source plane
     */
    public int getY(){
        return y;
    }
    
    @Override
    public String toString(){
        return String.format("Node#%d: (%d, %d)", id, x, y);
    }
}
