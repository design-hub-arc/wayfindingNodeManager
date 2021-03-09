package nodemanager.gui.mapComponents;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Adds zooming capabilities to the map images.
 * Shameless copy-paste from one of my other projects
 * 
 * @author Matt Crow
 */
public class MapZoomer implements MouseWheelListener {
    private final MapImage map;
    
    /**
     * The factor by which mouse wheel rotations are multiplied.
     * Higher values mean zooming occurs faster.
     */
    private static final double ZOOM_SPEED = 0.01;
    
    public MapZoomer(MapImage map){
        this.map = map;
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double dTheta = -e.getPreciseWheelRotation() * ZOOM_SPEED;
        map.zoom(dTheta);
    }

}
