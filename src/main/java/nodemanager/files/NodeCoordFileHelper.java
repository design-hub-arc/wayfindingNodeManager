package nodemanager.files;

import nodemanager.io.StreamReaderUtil;
import static nodemanager.io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import nodemanager.model.Graph;
import nodemanager.model.Node;

/**
 * @author Matt Crow
 */
public class NodeCoordFileHelper extends AbstractWayfindingFileHelper {
    
    public NodeCoordFileHelper(String title){
        super(title + "NodeCoords", FileType.NODE_COORD);
    }
    
    public NodeCoordFileHelper(){
        this("temp");
    }

    @Override
    public void readGraphDataFromFile(Graph g, InputStream in) throws IOException {
        String contents = StreamReaderUtil.readStream(in);
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
            g.addNode(new Node(id, x, y));
        }
    }

    @Override
    public void writeGraphDataToFile(Graph g, OutputStream out) throws IOException {
        out.write("id, x, y".getBytes());
        g.getAllNodes().forEach((node)->{
            try {
                out.write(String.format("%c%d, %d, %d", NEWLINE, node.getId(), node.getX(), node.getY()).getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
