package nodemanager.files;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import nodemanager.Session;
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
    public void setContents(InputStream s) throws IOException {
        content = ImageIO.read(ImageIO.createImageInputStream(s));
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
        Session.getCurrentDataSet().setMapImage(content);
        Session.map.setImage(content);
    }

    @Override
    public void exportData() {
        content = Session.map.getImage();
    }
    
    @Override
    public String toString(){
        return "MapFile " + content;
    }
}
