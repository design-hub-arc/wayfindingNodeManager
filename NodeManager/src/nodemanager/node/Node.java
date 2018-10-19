package nodemanager.node;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.awt.Graphics;
import nodemanager.gui.Scale;
import static java.lang.System.out;

public class Node {
    public final int id;
    public final double rawX;
    public final double rawY;
    private ArrayList<Integer> adjacentIds;
    private ArrayList<Node> adjacentNodes;
    
    private static HashMap<Integer, Node> allNodes = new HashMap<>();
    
    public Node(int id, double x, double y){
            this.id = id;
            rawX = x;
            rawY = y;
            adjacentIds = new ArrayList<>();
            adjacentNodes = new ArrayList<>();
        if(!allNodes.containsKey(id)){    
            allNodes.put(id, this);
        }
    }
    
    //how deal with not found?
    public static Node get(int nodeId){
        return allNodes.get(nodeId);
    }
    
    public static Collection<Node> getAll(){
        return allNodes.values();
    }
    
    
    public void addAdjId(int i){
        adjacentIds.add(i);
    }
    
    public static void init(){
        //need to run this to populate adjacent nodes
        //TODO add checking and make two way
        for(Node n : allNodes.values()){
            n.adjacentNodes.clear();
            for(Integer i : n.adjacentIds){
                n.adjacentNodes.add(allNodes.get(i));
            }
        }
    }
    
    public void draw(Graphics g, Scale s){
        g.setColor(Color.red);
        g.fillRect(s.x(rawX), s.y(rawY), s.percWidth(5), s.percHeight(5));
    }
    
    public void displayData(){
        out.println("Node #" + id);
        out.println("Raw: (" + rawX + ", " + rawY + ")");
        out.println("Adjacent: ");
        for(Integer i : adjacentIds){
            out.println("*" + i);
        }
    }
    public static void logAll(){
        out.println("*ALL NODES*");
        for(Node n : allNodes.values()){
            out.println("");
            n.displayData();
        }
    }
    
}
