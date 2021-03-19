package nodemanager.files;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import nodemanager.model.Graph;


/**
 * @author Matt Crow
 */
public class MapFileHelper extends AbstractWayfindingFileHelper{    
    private static final String FILE_FORMAT = "png";
    
    public MapFileHelper(String title) {
        super(title + "MapImage", FileType.MAP_IMAGE);
    }
    
    public MapFileHelper(){
        this("temp");
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
