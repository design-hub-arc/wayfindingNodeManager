package nodemanager.events;

import java.awt.image.BufferedImage;
import nodemanager.Session;
import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.model.Graph;

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
     * @param g the graph the map represents
     * @param m the MapImage that was changed
     * @param from the original image
     * @param to the image it was changed to
     */
    public MapResizeEvent(Graph g, MapImage m, BufferedImage from, BufferedImage to){
        super(g);
        resized = m;
        origImage = from;
        newImage = to;
    }

    @Override
    public void undoImpl(Graph g) {
        resized.setImage(origImage);
        g.setMapImage(origImage);
    }
    
    @Override
    public void redoImpl(Graph g){
        resized.setImage(newImage);
        g.setMapImage(newImage);
    }
}
