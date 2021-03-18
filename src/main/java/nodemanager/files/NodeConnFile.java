package nodemanager.files;

import java.io.InputStream;
import nodemanager.io.StreamReaderUtil;
import static nodemanager.io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
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

    @Override
    public void importData(Graph g) {
        connections.forEach((from, to)->{
            g.addConnection(from, to);
        });
    }

    @Override
    public void exportData(Graph g) {
        connections.clear();
        g.getAllConnections().forEach((Integer[] pair)->{
            connections.put(pair[0], pair[1]);
        });
    }
    
    @Override
    public String toString(){
        return getContentsToWrite();
    }

    @Override
    public void readGraphDataFromFile(Graph g, InputStream in) throws IOException {
        String fileContents = StreamReaderUtil.readStream(in);
        String[] lines = fileContents.split("\\n");
        
        String[] line;
        int id1;
        int id2;
        //skip header
        for(int i = 1; i < lines.length; i++){
            line = lines[i].split(",");
            id1 = Integer.parseInt(line[0].trim());
            id2 = Integer.parseInt(line[1].trim());
            g.addConnection(id1, id2);
        }
    }

    @Override
    public void writeGraphDataToFile(Graph g, OutputStream out) throws IOException {
        out.write("node1, node2".getBytes());
        g.getAllConnections().forEach((pair)->{
            try {
                out.write(String.format("%c%d, %d", NEWLINE, pair[0], pair[1]).getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
