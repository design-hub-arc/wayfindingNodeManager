package nodemanager.io;

import java.io.IOException;
import java.io.InputStream;
import nodemanager.Session;


/**
 * won't work due to text encoding?
 * Maybe this just needs to extent an ImageFile class, while others extend CsvFile?
 * 
 * @author Matt Crow
 */
public class MapFile extends AbstractWayfindingFile{
    public MapFile(String title) {
        super(title, FileType.PNG);
    }
    
    public MapFile(){
        this("temp");
    }

    @Override
    public String getContentsToWrite(){
        String ret = "";
        try{
            ret = Session.map.getImageAsString();
        } catch(IOException e){
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void readStream(InputStream s) {
        Session.map.setImage(s);
    }
    
}
