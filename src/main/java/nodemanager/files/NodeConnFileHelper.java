package nodemanager.files;

import java.io.InputStream;
import nodemanager.io.StreamReaderUtil;
import static nodemanager.io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.io.OutputStream;
import nodemanager.model.Graph;

/**
 * Used to read/write the node connections file
 * @author Matt Crow
 */
public class NodeConnFileHelper extends AbstractWayfindingFileHelper { 
    
    public NodeConnFileHelper(String title){
        super(title + "NodeConn", FileType.NODE_CONN);
    }
    
    public NodeConnFileHelper(){
        this("temp");
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
