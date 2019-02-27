package nodemanager.save;

import java.io.BufferedWriter;
import java.io.IOException;
import nodemanager.node.Node;

/**
 *
 * @author Matt Crow
 */
public class NodeCoordFile extends AbstractWayfindingFile{
    
    public NodeCoordFile(String title){
        super(title, FileType.CSV);
    }

    /**
     * writes the node data to a node coordinate file
     * @param buff a BufferedWriter connected to a csv file
     * @throws IOException
     */
    @Override
    public void writeContents(BufferedWriter buff) throws IOException{
        buff.write("id, x, y");
        for(Node n : Node.getAll()){
            buff.write(NL + n.getCoordLine());
        }
    }
    
    
    public static void main(String[] args){
        NodeCoordFile f = new NodeCoordFile("test");
        f.save("C:\\Users\\w1599227\\Desktop");
    }
}
