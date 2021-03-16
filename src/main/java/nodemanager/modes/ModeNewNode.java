package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;

/**
 *
 * @author Matt
 */
public final class ModeNewNode extends AbstractMode{

    public ModeNewNode() {
        super("Click on any location on the map to add a new node there.\nClick on an existing node to exit this mode.");
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        return this;
    }

}
