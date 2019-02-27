package nodemanager.save;

import nodemanager.node.Node;
import static nodemanager.save.AbstractWayfindingFile.NL;

/**
 * Used to read/write the node connections file
 * @author Matt Crow
 */
public class NodeConnFile extends AbstractWayfindingFile{
    public NodeConnFile(String title){
        super(title + "NodeConn", FileType.CSV);
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getContents(){
        StringBuilder s = new StringBuilder();
        Node.getAll().forEach((n) -> {
            n.getAdjIds().forEach((i) -> {
                s.append(NL + n.id + ", " + i);
            });
        });
        return s.toString();
    }
}
