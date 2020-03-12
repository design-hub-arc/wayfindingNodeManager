package files;

import static io.StreamReaderUtil.NEWLINE;
import java.io.InputStream;
import java.util.Arrays;
import nodemanager.Session;
import nodemanager.node.Node;
import nodemanager.node.NodeParser;

/**
 * Used as an interface to easily save the Node connection data
 * to either the computer or the google drive
 * @author Matt Crow
 */
public class NodeCoordFile extends AbstractCsvFile{
    public NodeCoordFile(String title){
        super(title + "NodeCoords", FileType.NODE_COORD);
    }
    
    public NodeCoordFile(){
        this("temp");
    }

    /**
     * generates the contents of a csv file containing the data of all the nodes used by the program.
     * The csv has three columns: the node's ID, and the x and y coordinates of that node's icon on the canvas
     * each row is a separate node.
     * @return the file's contents
     */
    @Override
    public final String getContentsToWrite(){
        StringBuilder s = new StringBuilder("id, x, y");
        
        Node.getAll().forEach((n) -> {
            s
                    .append(NEWLINE)
                    .append(n.id)
                    .append(", ")
                    .append(n.getIcon().getX())
                    .append(", ")
                    .append(n.getIcon().getY());
        });
        return s.toString();
    }

    /**
     * Reads an InputStream, creating new Nodes based on the data.
     * @param s an InputStream from a node file
     */
    @Override
    public void setContents(InputStream s) {
        NodeParser.parseFile(s, (line)->{
            System.out.println("In NodeCoordFile.setContents: " + Arrays.toString(line));
            Node.updateNode(
                Integer.parseInt(line[0].trim()),
                Integer.parseInt(line[1].trim()),
                Integer.parseInt(line[2].trim())
            );
            Session.map.addNode(Node.get(Integer.parseInt(line[0].trim())));
        });
        Session.map.refreshNodes();
    }

    @Override
    public void importData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
