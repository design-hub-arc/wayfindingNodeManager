package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;

/**
 *
 * @author Matt
 */
public final class ModeNone extends AbstractMode {

    public ModeNone() {
        super("");
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        return this;
    }

}
