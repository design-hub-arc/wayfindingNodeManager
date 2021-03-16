package nodemanager.modes;

import java.awt.event.MouseEvent;
import nodemanager.NodeManager;
import nodemanager.Session;
import nodemanager.events.ConnectionAddedEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.gui.editPage.mapComponents.NodeIcon;
import nodemanager.model.Graph;

/**
 *
 * @author Matt
 */
public class ModeAddConnection extends AbstractMode {
    private int nodeId;
    public ModeAddConnection(int id){
        super(String.format(
            "Click on another node to connect it to node %d\n"
            +"You can instead right click on another node to add a connection, " 
            + "then switch to that node", id)
        );
        this.nodeId = id;
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        AbstractMode newMode = this;
        NodeIcon clickedOn = mapImage.hoveredNodeIcon(me.getX(), me.getY());
        if(clickedOn != null){
            Graph g = mapImage.getGraph();
            if(g.addConnection(nodeId, clickedOn.getNode().getId())){
                NodeManager.getInstance().getLog().log(new ConnectionAddedEvent(g, nodeId, clickedOn.getNode().getId()));
                mapImage.repaint();
            }
            
            if(me.getButton() > 1){ // right click
                newMode = new ModeAddConnection(clickedOn.getNode().getId());
            }
        }
        return newMode;
    }
}
