package nodemanager.files;

import nodemanager.io.StreamReaderUtil;
import static nodemanager.io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import nodemanager.Session;
import nodemanager.model.Graph;
import nodemanager.node.Node;

/**
 * @author Matt Crow
 */
public class NodeCoordFile extends AbstractCsvFile{
    private final ArrayList<Node> nodes;
    
    public NodeCoordFile(String title){
        super(title + "NodeCoords", FileType.NODE_COORD);
        nodes = new ArrayList<>();
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
        nodes.forEach((n) -> {
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
    public void setContents(InputStream s) throws IOException {
        nodes.clear();
        String contents = StreamReaderUtil.readStream(s);
        String[] rows = contents.split("\\n");
        
        String[] line;
        int id;
        int x;
        int y;
        for(int i = 1; i < rows.length; i++){
            line = rows[i].split(",");
            id = Integer.parseInt(line[0].trim());
            x = Integer.parseInt(line[1].trim());
            y = Integer.parseInt(line[2].trim());
            nodes.add(new Node(id, x, y));
        }
    }

    @Override
    public void importData() {
        Graph g = Session.getCurrentDataSet();
        nodes.forEach((n)->{
            g.addNode(n);
            Session.map.addNode(n);
        });
        Session.map.refreshNodes();
    }

    @Override
    public void exportData() {
        nodes.clear();
        Session.getCurrentDataSet().getAllNodes().forEach((n)->{
            nodes.add(n);
        });
    }
    
    @Override
    public String toString(){
        return getContentsToWrite();
    }
}
