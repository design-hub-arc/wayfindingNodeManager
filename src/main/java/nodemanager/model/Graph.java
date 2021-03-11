package nodemanager.model;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import nodemanager.node.Node;

/**
 * A Graph is a collection of a map image, nodes, connections, and labels.
 * 
 * @author Matt Crow
 */
public class Graph {
    private BufferedImage mapImage;
    private final HashMap<Integer, Node> nodes;
    private final HashMap<Integer, Integer> connections;
    private final HashMap<String, Integer> labels;
    
    public Graph(){
        mapImage = null;
        nodes = new HashMap<>();
        connections = new HashMap<>();
        labels = new HashMap<>();
    }
    
    public final Node getNodeById(int id){
        return nodes.get(id);
    }
    
    public final int[] getConnectionsById(int id){
        return connections.entrySet().stream().filter((entry)->{
            return entry.getKey() == id;
        }).map((entry)->{
            return entry.getValue();
        }).mapToInt((i)->{
            return i.intValue();
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
}
