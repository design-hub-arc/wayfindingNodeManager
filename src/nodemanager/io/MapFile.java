package nodemanager.io;

import java.io.InputStream;
import nodemanager.gui.MapImage;

/**
 * won't work due to text encoding?
 * @author Matt Crow
 */
public class MapFile extends AbstractWayfindingFile{
    private final MapImage image;
    
    public MapFile(String title, MapImage m) {
        super(title, FileType.PNG);
        image = m;
    }
    
    public MapFile(MapImage m){
        this("temp", m);
    }

    @Override
    public String getContentsToWrite() {
        throw new UnsupportedOperationException("cannot express image as a String!");
    }

    @Override
    public void readStream(InputStream s) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
