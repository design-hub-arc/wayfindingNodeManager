package nodemanager.node;

import java.util.*;
import nodemanager.gui.NodeIcon;
import static java.lang.System.out;

public class Node {
    public final int id;
    public final double rawX;
    public final double rawY;
    private HashSet<Integer> adjacentIds;
    private NodeIcon icon;
    
    private static HashMap<Integer, Node> allNodes = new HashMap<>();
    private static int nextId = 0;
    
    public Node(int id, double x, double y){
            this.id = id;
            rawX = x;
            rawY = y;
            adjacentIds = new HashSet<>();
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
        allNodes.remove(id);
        
        Node n = get(id);
        if(n != null){
            n.adjacentIds.stream().forEach(i -> get(i).adjacentIds.remove(Integer.valueOf(id)));
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
    public void removeAdj(int i){
        Integer remId = Integer.valueOf(i);
        if(adjacentIds.contains(remId)){
            Node connected = Node.get(i);
            adjacentIds.remove(remId);
            if(connected.adjacentIds.contains(remId)){
                connected.removeAdj(id);
            }
        }
    }
    public HashSet<Integer> getAdjIds(){
        return adjacentIds;
    }
    
    
    
    private void checkOneWay(){
        Node adj;
        for(Integer i : adjacentIds){
            adj = Node.get(i);
            if(adj != null){
                if(Arrays.asList(adj.adjacentIds).indexOf(id) == -1){
                    adj.addAdjId(id);
                }
            }
        }
    }
    public void init(){
        HashSet<Integer> newAdj = new HashSet<>();
        adjacentIds.stream().filter(aid -> allNodes.containsKey(aid)).forEach(adid -> newAdj.add(adid));
        adjacentIds = newAdj;
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
    
    public static void initAll(){
        //need to run this to populate adjacent nodes
        //TODO add checking and make two way
        for(Node n : allNodes.values()){
            n.checkOneWay();
        }
        for(Node n : allNodes.values()){
            n.init();
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
