package nodemanager.events;

import nodemanager.Session;
import nodemanager.gui.mapComponents.MapImage;
import nodemanager.model.Node;

/**
 * Recorded when a node was deleted via the "delete this node" button
 * 
 * I can see this being problematic if it can create multiple nodes with the same ID
 * 
 * @author Matt Crow
 */
public class NodeDeleteEvent extends EditEvent{
    private final Node nodeDeleted;
    private final MapImage removedFrom;
    
    public NodeDeleteEvent(Node n, MapImage m){
        nodeDeleted = n;
        removedFrom = m;
    }
    
    @Override
    public void undo() {
        Session.getCurrentDataSet().addNode(nodeDeleted);
        //nodeDeleted.getAdjIds().forEach(id -> Node.get(id).addAdjId(nodeDeleted.id)); //reconnect
        removedFrom.addNode(nodeDeleted);
        removedFrom.repaint();
    }

    @Override
    public void redo() {
        Session.getCurrentDataSet().removeNode(nodeDeleted.id);
        removedFrom.removeNode(nodeDeleted);
        removedFrom.repaint();
    }
    
    
}
