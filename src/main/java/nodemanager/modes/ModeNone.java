package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.NodeManager;
import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.gui.editPage.mapComponents.NodeIcon;

/**
 *
 * @author Matt
 */
public final class ModeNone extends AbstractMode {

    public ModeNone() {
        super("(back to no mode)");
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        NodeIcon clicked = mapImage.hoveredNodeIcon(me.getX(), me.getY());
        if(clicked != null){
            NodeManager.getInstance().getNodeDataPane().selectNode(clicked.getNode());
        }
        return this;
    }

}
