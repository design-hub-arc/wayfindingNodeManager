package nodemanager.node;

import java.io.*;
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
        allNodes.put(id, this);
        if(id >= nextId){
            nextId = id + 1;
        }
    }
    
    public Node(double x, double y){
        this(nextId, x, y);
    }
    
    public static void removeNode(int id){
        
        Node n = get(id);
        if(n != null){
            n.adjacentIds.stream().forEach(i -> get(i).adjacentIds.remove(Integer.valueOf(id)));
        }
        allNodes.remove(id);
    }
    public static void removeAll(){
        allNodes.clear();
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
    
    public static void generateDataAt(String path){
        BufferedWriter out = null;
        String nl = System.getProperty("line.separator");
        try {
            File nodeFile = new File(path + File.separator + "nodeData.csv");
            File connectFile = new File(path + File.separator + "nodeConnections.csv");
            
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
