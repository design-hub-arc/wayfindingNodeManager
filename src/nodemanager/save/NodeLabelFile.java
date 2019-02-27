package nodemanager.save;

import nodemanager.node.Node;

/**
 *
 * @author Matt Crow
 */
public class NodeLabelFile extends AbstractWayfindingFile{
    public NodeLabelFile(String title){
        super(title + "Labels", FileType.CSV);
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getContents(){
        StringBuilder s = new StringBuilder("label, id" + NL);
        for(Node n : Node.getAll()){
            s.append(n.getLabelLines());
        }
        return s.toString();
    }
}
