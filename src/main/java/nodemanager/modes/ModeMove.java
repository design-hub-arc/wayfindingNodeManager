package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;

/**
 *
 * @author Matt
 */
public class ModeMove extends AbstractMode {

    public ModeMove() {
        super("Click on a location on the map to move this node there");
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        return this;
    }

}
