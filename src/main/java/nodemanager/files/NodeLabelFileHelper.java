package nodemanager.files;

import nodemanager.io.StreamReaderUtil;
import static nodemanager.io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import nodemanager.model.Graph;

/**
 *
 * @author Matt Crow
 */
public class NodeLabelFileHelper extends AbstractWayfindingFileHelper {
    
    public NodeLabelFileHelper(String title){
        super(title + "Labels", FileType.LABEL);
    }
    
    public NodeLabelFileHelper(){
        this("temp");
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
                out.write(String.format("%c%s, %d", NEWLINE, label, g.getNodeByLabel(label).getId()).getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}
