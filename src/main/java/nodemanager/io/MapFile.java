package nodemanager.io;

import java.io.*;
import javax.imageio.ImageIO;
import nodemanager.Session;


/**
 * won't work due to text encoding?
 * Maybe this just needs to extent an ImageFile class, while others extend CsvFile?
 * 
 * @author Matt Crow
 */
public class MapFile extends AbstractWayfindingFile{
    public MapFile(String title) {
        super(title + "MapImage", FileType.MAP_IMAGE);
    }
    
    public MapFile(){
        this("temp");
    }

    @Override
    public void readStream(InputStream s) {
        try {
            Session.map.setImage(ImageIO.read(ImageIO.createImageInputStream(s)));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void writeToFile(File f){
        try{
            ImageIO.write(Session.map.getImage(), "png", f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
