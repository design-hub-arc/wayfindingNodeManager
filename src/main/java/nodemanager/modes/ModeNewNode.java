package nodemanager.modes;

import java.awt.Point;
import java.awt.event.MouseEvent;
import nodemanager.NodeManager;
import nodemanager.events.NodeCreateEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.gui.editPage.mapComponents.NodeIcon;
import nodemanager.model.Node;

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
        AbstractMode newMode = this;
        
        NodeIcon hoveringOver = mapImage.hoveredNodeIcon(me.getX(), me.getY());
        if(hoveringOver == null){
            Point clicked = mapImage.mouseClickToNodeSpace(me.getPoint());
            Node n = mapImage.getGraph().createNode(
                clicked.x, 
                clicked.y
            );
            mapImage.getGraph().addNode(n);
            mapImage.addNode(n);
            NodeManager.getInstance().getLog().log(new NodeCreateEvent(mapImage.getGraph(), n, mapImage));
            mapImage.repaint();
        } else {
            newMode = new ModeNone();
        }
        
        return newMode;
    }

}
