package nodemanager.modes;

import java.awt.Point;
import java.awt.event.MouseEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.gui.editPage.mapComponents.NodeIcon;
import nodemanager.model.Node;

/**
 * Older versions allowed us to watch the node move.
 * Not sure if we still want that.
 * 
 * @author Matt
 */
public class ModeMove extends AbstractMode {
    private final Node movingNode;
    
    public ModeMove(Node movingNode) {
        super(String.format("Click on a location on the map to move node %d there", movingNode.getId()));
        this.movingNode = movingNode;
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        NodeIcon mapIcon = mapImage.getIcon(movingNode.getId());
        Point newCoords = mapImage.mouseClickToNodeSpace(me.getPoint());
        movingNode.setX(newCoords.x);
        movingNode.setY(newCoords.y);
        mapIcon.nodePosUpdated();
        return new ModeNone();
    }

}
