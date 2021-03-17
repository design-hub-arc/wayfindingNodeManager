package nodemanager.modes;

import java.awt.Point;
import java.awt.event.MouseEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;

/**
 *
 * @author Matt
 */
public class ModeRescaleUpperLeft extends AbstractMode {

    public ModeRescaleUpperLeft() {
        super("Click on a point on the new map to set the new upper-left corner");
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        // problem: me is where the user clicked on the COMPONENT not on the MAP IMAGE
        Point p = mapImage.mouseClickToNodeSpace(me.getPoint());
        //mapImage.setScaleOrigin(p.x, p.y);
        
        return new ModeRescaleLowerRight(me.getPoint());
    }

}
