package nodemanager.files;

import java.io.InputStream;
import nodemanager.node.Node;
import nodemanager.io.StreamReaderUtil;
import static nodemanager.io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.util.HashMap;
import nodemanager.Session;
import nodemanager.model.Graph;

/**
 * Used to read/write the node connections file
 * @author Matt Crow
 */
public class NodeConnFile extends AbstractCsvFile{
    
    private final HashMap<Integer, Integer> connections;
    
    public NodeConnFile(String title){
        super(title + "NodeConn", FileType.NODE_CONN);
        connections = new HashMap<>();
    }
    
    public NodeConnFile(){
        this("temp");
    }

    /**
     * generates the contents of a csv file containing the node connection data.
     * The first column is one node id, and the second column is another.
     * each row represents a connection between nodes.
     * @return the file's contents
     */
    @Override
    public String getContentsToWrite(){
        StringBuilder s = new StringBuilder("node1, node2");
        connections.forEach((from, to)->{
            s
                .append(NEWLINE)
                .append(Integer.toString(from))
                .append(", ")
                .append(Integer.toString(to));
        });
        return s.toString();
    }

    /**
     * 
     * @param s an InputStream from a connection file
     * @throws java.io.IOException
     */
    @Override
    public void setContents(InputStream s) throws IOException {
        connections.clear();
        
        String fileContents = StreamReaderUtil.readStream(s);
        String[] lines = fileContents.split("\\n");
        
        String[] line;
        int id1;
        int id2;
        //skip header
        for(int i = 1; i < lines.length; i++){
            line = lines[i].split(",");
            id1 = Integer.parseInt(line[0].trim());
            id2 = Integer.parseInt(line[1].trim());
            connections.put(id1, id2);
        }
    }

    @Override
    public void importData(Graph g) {
        connections.forEach((from, to)->{
            g.addConnection(from, to);
        });
    }

    @Override
    public void exportData() {
        connections.clear();
        Session.getCurrentDataSet().getAllConnections().forEach((Integer[] pair)->{
            connections.put(pair[0], pair[1]);
        });
    }
    
    @Override
    public String toString(){
        return getContentsToWrite();
    }
}
