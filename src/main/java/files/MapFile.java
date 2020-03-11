package files;

import java.awt.image.BufferedImage;
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
    private BufferedImage content;
    
    public MapFile(String title) {
        super(title + "MapImage", FileType.MAP_IMAGE);
    }
    
    public MapFile(){
        this("temp");
    }

    @Override
    public void setContents(InputStream s) {
        try {
            content = ImageIO.read(ImageIO.createImageInputStream(s));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void importData(){
        if(content == null){
            throw new NullPointerException("Content must be set before importing");
        }
        Session.map.setImage(content);
        Session.map.repaint();
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
