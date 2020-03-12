package files;

import io.StreamReaderUtil;
import static io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import nodemanager.node.Node;

/**
 *
 * @author Matt Crow
 */
public class NodeLabelFile extends AbstractCsvFile{
    private final HashMap<String, Integer> labelToId;
    
    public NodeLabelFile(String title){
        super(title + "Labels", FileType.LABEL);
        labelToId = new HashMap<>();
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
        labelToId.forEach((label, id)->{
            s
                .append(NEWLINE)
                .append(label)
                .append(", ")
                .append(Integer.toString(id));
        });
        return s.toString();
    }

    @Override
    public void setContents(InputStream s) throws IOException {
        labelToId.clear();
        String contents = StreamReaderUtil.readStream(s);
        String[] rows = contents.split("\\n");
        
        String[] line;
        String label;
        int id;
        for(int i = 1; i < rows.length; i++){
            line = rows[i].split(",");
            label = line[0].trim();
            id = Integer.parseInt(line[1].trim());
            labelToId.put(label, id);
        }
    }

    @Override
    public void importData() {
        labelToId.forEach((label, id)->{
            if(Node.get(id) == null){
                new Node(id);
            }
            Node.get(id).addLabel(label);
        });
    }

    @Override
    public void exportData() {
        labelToId.clear();
        Node.getAll().forEach((node)->{
            for(String label : node.getLabels()){
                labelToId.put(label, node.id);
            }
        });
    }
    
    @Override
    public String toString(){
        return getContentsToWrite();
    }
}
