package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;

/**
 *
 * @author Matt
 */
public class ModeRescaleLowerRight extends AbstractMode {

    public ModeRescaleLowerRight() {
        super("Position the upper left corner of node -2 at the lower right corner of where you want to crop");
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        return this;
    }

}
