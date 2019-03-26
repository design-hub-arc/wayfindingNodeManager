package nodemanager.io;

import java.io.InputStream;
import nodemanager.node.Node;
import nodemanager.node.NodeParser;

/**
 *
 * @author Matt Crow
 */
public class NodeLabelFile extends AbstractCsvFile{
    public NodeLabelFile(String title){
        super(title + "Labels", FileType.LABEL);
    }
    
    public NodeLabelFile(){
        this("temp");
    }

    /**
     * generates the contents of a csv file containing all the labels used by the program.
     * The csv has two columns: a label, then the id of the node that has that label.
     * @return the file's contents
     */
    @Override
    public String getContentsToWrite(){
        StringBuilder s = new StringBuilder("label, id");
        for(Node n : Node.getAll()){
            for(String l : n.getLabels()){
                s
                        .append(NL)
                        .append(l)
                        .append(", ")
                        .append(n.id);
            }
        }
        return s.toString();
    }

    @Override
    public void readStream(InputStream s) {
        NodeParser.parseFile(s, (line)->{
            Node.get(Integer.parseInt(line[1].trim())).addLabel(line[0].trim());
        });
    }
}
