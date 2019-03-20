package nodemanager.io;

import java.io.InputStream;
import nodemanager.node.Node;
import nodemanager.node.NodeParser;
import static nodemanager.io.AbstractWayfindingFile.NL;

/**
 * Used to read/write the node connections file
 * @author Matt Crow
 */
public class NodeConnFile extends AbstractCsvFile{
    public NodeConnFile(String title){
        super(title + "NodeConn", FileType.NODE_CONN);
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
        Node.getAll().forEach((n) -> {
            n.getAdjIds().forEach((i) -> {
                s
                        .append(NL)
                        .append(n.id)
                        .append(", ")
                        .append(i);
            });
        });
        return s.toString();
    }

    /**
     * Reads an input stream, adding connections to nodes based on the data
     * @param s an InputStream from a connection file
     */
    @Override
    public void readStream(InputStream s) {
        NodeParser.parseFile(s, (line)->{
            Node.get(Integer.parseInt(line[0].trim())).addAdjId(Integer.parseInt(line[1].trim()));
        });
    }
}
