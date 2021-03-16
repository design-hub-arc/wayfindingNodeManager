package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;

/**
 *
 * @author Matt Crow
 */
public abstract class AbstractMode {
    private final String message;
    
    public AbstractMode(String message){
        this.message = message;
    }
    
    /**
     * Called by a MapImage when it is clicked. 
     * 
     * @param mapImage the map image which was clicked
     * @param me where it was clicked
     * 
     * @return the mode to update the application to
     */
    public abstract AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me);
    
    public final String getMessage(){
        return message;
    }
}
