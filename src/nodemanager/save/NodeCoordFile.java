package nodemanager.save;

import nodemanager.node.Node;

/**
 * Used as an interface to easily save the Node connection data
 * to either the computer or the google drive
 * @author Matt Crow
 */
public class NodeCoordFile extends AbstractWayfindingFile{
    public NodeCoordFile(String title){
        super(title + "NodeCoords", FileType.CSV);
    }

    /**
     * gets 
     * @return 
     */
    @Override
    public final String getContents(){
        StringBuilder s = new StringBuilder("id, x, y");
        
        Node.getAll().forEach((n) -> {
            s.append(NL).append(n.getCoordLine());
        });
        return s.toString();
    }
}
