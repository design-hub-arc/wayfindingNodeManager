package nodemanager.node;

import java.util.*;
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
    
    public ArrayList<Node> getAdj(){
        return adjacentNodes;
    }
    
    public static void init(){
        //need to run this to populate adjacent nodes
        //TODO add checking and make two way
        for(Node n : allNodes.values()){
            n.adjacentNodes.clear();
            for(Integer i : n.adjacentIds){
                n.adjacentNodes.add(allNodes.get(i));
                /*
                if(Arrays.asList(allNodes.get(i).adjacentIds).indexOf(n.id) == -1){
                    allNodes.get(i).addAdjId(n.id);
                }*/
            }
        }
    }
    
    public String getDesc(){
        String ret = 
                "Node #" + id + System.lineSeparator() +
                "Raw coordinates: (" + rawX + ", " + rawY + ")" + System.lineSeparator() +
                "Adjacent ids: " + System.lineSeparator();
        //streaming doesn't work here
        for(int i : adjacentIds){
            ret += ("* " + i + System.lineSeparator());
        }
        return ret;
    }
    
    public void displayData(){
        out.println(getDesc());
    }
    public static void logAll(){
        out.println("*ALL NODES*");
        for(Node n : allNodes.values()){
            out.println("");
            n.displayData();
        }
    }
    
}
