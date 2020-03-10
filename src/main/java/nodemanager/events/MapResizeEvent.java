package nodemanager.events;

import java.awt.image.BufferedImage;
import nodemanager.Session;
import nodemanager.gui.MapImage;

/**
 *
 * @author Matt Crow
 */
public class MapResizeEvent extends EditEvent{
    private final MapImage resized;
    private final BufferedImage origImage;
    private final BufferedImage newImage;
    
    /**
     * Created by MapImage whenever the user changes the map.
     * @param m the MapImage that was changed
     * @param from the original image
     * @param to the image it was changed to
     */
    public MapResizeEvent(MapImage m, BufferedImage from, BufferedImage to){
        resized = m;
        origImage = from;
        newImage = to;
    }

    @Override
    public void undo() {
        resized.setImage(origImage);
        Session.newMapX = 0;
        Session.newMapY = 0;
    }
    
    @Override
    public void redo(){
        resized.setImage(newImage);
        Session.newMapX = 0;
        Session.newMapY = 0;
    }
}
