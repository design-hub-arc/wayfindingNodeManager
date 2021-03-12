package nodemanager.model;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import nodemanager.node.Node;

/**
 * A Graph is a collection of a map image, nodes, connections, and labels.
 * 
 * @author Matt Crow
 */
public class Graph {
    private BufferedImage mapImage;
    private final HashMap<Integer, Node> nodes;
    private final HashMap<Integer, HashSet<Integer>> connections;
    private final HashMap<String, Integer> labels;
    
    private int nextNodeId;
    
    public Graph(){
        mapImage = null;
        nodes = new HashMap<>();
        connections = new HashMap<>();
        labels = new HashMap<>();
        nextNodeId = 0;
    }
    
    public final Node createNode(int x, int y){
        Node n = new Node(nextNodeId, x, y);
        nextNodeId++;
        return n;
    }
    public final void addNode(Node n){
        nodes.put(n.getId(), n);
        if(n.getId() >= nextNodeId){
            nextNodeId = n.getId() + 1;
        }
    }
    
    public final void addConnection(int fromId, int toId){
        if(!connections.containsKey(fromId)){
            connections.put(fromId, new HashSet<>());
        }
        if(!connections.containsKey(toId)){
            connections.put(toId, new HashSet<>());
        }
        connections.get(fromId).add(toId);
        connections.get(toId).add(fromId);
    }
    
    public final void addLabel(String label, int id){
        labels.put(label.toUpperCase(), id);
    }
    
    public final void setMapImage(BufferedImage buff){
        this.mapImage = buff;
    }
    
    public final void removeNode(int id){
        this.nodes.remove(id);
    }
    
    public final void removeConnection(int fromId, int toId){
        if(connections.containsKey(fromId)){
            connections.get(fromId).remove(toId);
        }
        if(connections.containsKey(toId)){
            connections.get(toId).remove(fromId);
        }
    }
    
    public final List<Node> getAllNodes(){
        return nodes.values().stream().collect(Collectors.toList());
    }
    
    public final List<Integer[]> getAllConnections(){
        LinkedList<Integer[]> pairs = new LinkedList<>();
        connections.entrySet().forEach((entry)->{
            entry.getValue().forEach((to) -> {
                pairs.add(new Integer[]{entry.getKey(), to});
            });
        });
        return pairs;
    }
    
    public final List<String> getAllLabel(){
        return labels.keySet().stream().collect(Collectors.toList());
    }
    
    public final BufferedImage getMapImage(){
        return mapImage;
    }
    
    public final Node getNodeById(int id){
        return nodes.get(id);
    }
    
    public final Node getNodeByLabel(String label){
        return nodes.get(labels.get(label.toUpperCase()));
    }
    
    public final int[] getConnectionsById(int id){
        return connections.getOrDefault(id, new HashSet<>()).stream().mapToInt((integer)->{
            return integer.intValue();
        }).toArray();
    }
    
    public final String[] getLabelsById(int id){
        return labels.entrySet().stream().filter((entry)->{
            return entry.getValue() == id;
        }).map((entry)->{
            return entry.getKey();
        }).toArray((size)->new String[size]);
    }
    
    public final String getDescriptionForNode(int nodeId){
        StringBuilder sb = new StringBuilder();
        Node node = getNodeById(nodeId);
        if(node == null){
            sb.append(String.format("Couldn't find node with id %d", nodeId));
        } else {
            sb.append(String.format("%s%n", node.toString()));
            sb.append("* Connections:\n");
            for(int conns : getConnectionsById(node.getId())){
                sb.append(String.format("\t%d%n", conns));
            }
            sb.append("* Labels:\n");
            for(String label : getLabelsById(node.getId())){
                sb.append(String.format("\t%s%n", label));
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Graph data:\n");
        sb.append("* Map Image:\n");
        sb.append(String.format("\t%s%n", (mapImage == null) ? "NULL" : mapImage.toString()));
        sb.append("* Nodes:\n");
        nodes.forEach((id, node)->{
            sb.append(String.format("\t%d => %s%n", id, node.toString()));
        });
        sb.append("* Connections:\n");
        connections.forEach((from, to)->{
            sb.append(String.format("\t%d => %d%n", from, to));
        });
        sb.append("* Labels:\n");
        labels.forEach((label, id)->{
            sb.append(String.format("\t%s => %d%n", label, id));
        });
        return sb.toString();
    }
    
    public static final Graph createDefault(){
        Graph ret = new Graph();
        ret.addNode(new Node(-1, 0, 0));
        ret.addNode(new Node(-2, 100, 100));
        ret.mapImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        return ret;
    }
}
