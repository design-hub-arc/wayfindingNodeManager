package nodemanager.files;

import nodemanager.io.StreamReaderUtil;
import static nodemanager.io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import nodemanager.model.Graph;

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
    public void importData(Graph g) {
        labelToId.forEach((label, id)->{
            g.addLabel(label, id);
        });
    }

    @Override
    public void exportData(Graph g) {
        labelToId.clear();
        g.getAllLabel().forEach((label)->{
            labelToId.put(label, g.getNodeByLabel(label).getId());
        });
    }
    
    @Override
    public String toString(){
        return getContentsToWrite();
    }

    @Override
    public void readGraphDataFromFile(Graph g, InputStream in) throws IOException {
        String contents = StreamReaderUtil.readStream(in);
        String[] rows = contents.split("\\n");
        
        String[] line;
        String label;
        int id;
        for(int i = 1; i < rows.length; i++){
            line = rows[i].split(",");
            label = line[0].trim();
            id = Integer.parseInt(line[1].trim());
            g.addLabel(label, id);
        }
    }

    @Override
    public void writeGraphDataToFile(Graph g, OutputStream out) throws IOException {
        out.write("label, id".getBytes());
        g.getAllLabel().forEach((label)->{
            try {
                out.write(String.format("%c%s, %d", label, g.getNodeByLabel(label).getId()).getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
