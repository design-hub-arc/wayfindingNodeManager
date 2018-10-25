package nodemanager.node;

import java.util.*;
import nodemanager.gui.NodeIcon;
import static java.lang.System.out;

public class Node {
    public final int id;
    public final double rawX;
    public final double rawY;
    private ArrayList<Integer> adjacentIds;
    private ArrayList<Node> adjacentNodes;
    private NodeIcon icon;
    
    private static HashMap<Integer, Node> allNodes = new HashMap<>();
    
    private static int nextId = 0;
    
    public Node(int id, double x, double y){
            this.id = id;
            rawX = x;
            rawY = y;
            adjacentIds = new ArrayList<>();
            adjacentNodes = new ArrayList<>();
            icon = new NodeIcon(this);
        if(!allNodes.containsKey(id)){    
            allNodes.put(id, this);
            if(id >= nextId){
                nextId = id + 1;
            }
        }
    }
    
    public Node(double x, double y){
        this(nextId, x, y);
    }
    
    public static void removeNode(int id){
        Node n = get(id);
        if(n != null){
            allNodes.remove(id);
            n.adjacentIds.stream().forEach(i -> get(i).adjacentIds.remove(Integer.valueOf(id)));
            n.adjacentNodes.stream().forEach(nn -> nn.adjacentNodes.remove(n));
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
    
    public static void initAll(){
        //need to run this to populate adjacent nodes
        //TODO add checking and make two way
        for(Node n : allNodes.values()){
            n.init(true);
        }
    }
    public void init(boolean cascade){
        adjacentNodes.clear();
        Node adj;
        for(Integer i : adjacentIds){
            adj = allNodes.get(i);
            adjacentNodes.add(adj);
            /*
            Make connection table which works both ways:
            1, 2 adds 2 to 1's adjIds, and 1 to 2's
            
            if(Arrays.asList(adj.adjacentIds).indexOf(id) == -1){
                adj.addAdjId(id);
                if(cascade){
                    adj.init(false);
                }
            }*/
        }
    }
    
    public NodeIcon getIcon(){
        return icon;
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
