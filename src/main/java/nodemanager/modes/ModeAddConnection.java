package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;

/**
 *
 * @author Matt
 */
public class ModeAddConnection extends AbstractMode {
    private int nodeId;
    public ModeAddConnection(int id){
        super(String.format("Click on another node to connect it to node %d", id));
        this.nodeId = id;
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        return this;
    }
}
