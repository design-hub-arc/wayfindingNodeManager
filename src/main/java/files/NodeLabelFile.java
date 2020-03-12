package files;

import static io.StreamReaderUtil.NEWLINE;
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
                        .append(NEWLINE)
                        .append(l)
                        .append(", ")
                        .append(n.id);
            }
        }
        return s.toString();
    }

    @Override
    public void setContents(InputStream s) {
        NodeParser.parseFile(s, (line)->{
            int id = Integer.parseInt(line[1].trim());
            if(Node.get(id) == null){
                new Node(id);
            }
            Node.get(id).addLabel(line[0].trim());
        });
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
