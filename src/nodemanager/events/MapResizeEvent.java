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
    
    public MapResizeEvent(MapImage m, BufferedImage bi){
        resized = m;
        origImage = bi;
    }

    @Override
    public void undo() {
        resized.setImage(origImage);
        Session.newMapX = 0;
        Session.newMapY = 0;
    }
    
}
