package nodemanager.gui.mapComponents;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Adds the ability to drag the map image around.
 * Shameless copy-paste from one of my other projects.
 * 
 * Note: need to add this class as BOTH a mouse and mouseMotion listener
 * 
 * @author Matt Crow
 */
public class MapPanner extends MouseAdapter {
    private final MapImage map;
    private int prevX;
    private int prevY;
    
    public MapPanner(MapImage map){
        super();
        this.map = map;
        prevX = -1;
        prevY = -1;
    }
    
    @Override
    public void mousePressed(MouseEvent e){
        prevX = e.getX();
        prevY = e.getY();
    }
    
    @Override
    public void mouseDragged(MouseEvent e){
        int dx = e.getX() - prevX;
        int dy = e.getY() - prevY;
        prevX = e.getX();
        prevY = e.getY();
        // do panning
        map.pan(-dx, -dy);
    }
}
