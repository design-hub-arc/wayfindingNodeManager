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
        // translates from where the component is clicked to a coordinate on the image
        return new ModeRescaleLowerRight(new Point(
            mapImage.translateClickX(me.getX()),
            mapImage.translateClickY(me.getY())
        ));
    }

}
