package nodemanager.files;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import nodemanager.model.Graph;


/**
 * @author Matt Crow
 */
public class MapFile extends AbstractWayfindingFile{
    private BufferedImage content;
    
    private static final String FILE_FORMAT = "png";
    
    public MapFile(String title) {
        super(title + "MapImage", FileType.MAP_IMAGE);
    }
    
    public MapFile(){
        this("temp");
    }
    
    @Override
    public void writeToFile(File f) throws IOException{
        if(content == null){
            throw new NullPointerException("Cannot write to file before contents are set");
        }
        ImageIO.write(content, FILE_FORMAT, f);
    }
    
    @Override
    public void importData(Graph g){
        if(content == null){
            throw new NullPointerException("Content must be set before importing");
        }
        g.setMapImage(content);
        //Session.map.setImage(content);
    }

    @Override
    public void exportData(Graph g) {
        content = g.getMapImage();
    }
    
    @Override
    public String toString(){
        return "MapFile " + content;
    }

    @Override
    public void readGraphDataFromFile(Graph g, InputStream in) throws IOException {
        BufferedImage mapImage = ImageIO.read(in);
        g.setMapImage(mapImage);
    }

    @Override
    public void writeGraphDataToFile(Graph g, OutputStream out) throws IOException {
        ImageIO.write(g.getMapImage(), FILE_FORMAT, out);
    }
}
