package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.NodeManager;
import nodemanager.Session;
import nodemanager.events.ConnectionRemovedEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.gui.editPage.mapComponents.NodeIcon;
import nodemanager.model.Graph;

/**
 *
 * @author Matt
 */
public class ModeRemoveConnection extends AbstractMode {
    private int nodeId;
    
    public ModeRemoveConnection(int nodeId) {
        super(String.format(
            "Click on a node to disconnect it from node %d\n"
            + "You can instead right click on another nod to remove a connections, "
            + "then switch to that node", nodeId));
        this.nodeId = nodeId;
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        AbstractMode newMode = this;
        NodeIcon clickedOn = mapImage.hoveredNodeIcon(me.getX(), me.getY());
        if(clickedOn != null){
            Graph g = mapImage.getGraph();
            if(g.removeConnection(nodeId, clickedOn.getNode().getId())){
                NodeManager.getInstance().getLog().log(new ConnectionRemovedEvent(g, nodeId, clickedOn.getNode().getId()));
                mapImage.repaint();
            }
            
            if(me.getButton() > 1){//right click
                newMode = new ModeRemoveConnection(clickedOn.getNode().getId());
            }
        }
        return newMode;
    }

}
