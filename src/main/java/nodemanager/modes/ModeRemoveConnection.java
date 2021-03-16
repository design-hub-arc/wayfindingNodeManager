package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;

/**
 *
 * @author Matt
 */
public class ModeRemoveConnection extends AbstractMode {
    private int nodeId;
    
    public ModeRemoveConnection(int nodeId) {
        super(String.format("Click on a node to disconnect it from node %d", nodeId));
        this.nodeId = nodeId;
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        return this;
    }

}
