package nodemanager.events;

import nodemanager.gui.MapImage;
import nodemanager.node.Node;

/**
 * Recorded when a node was deleted via the "delete this node" button
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
        Node.addNode(nodeDeleted);
        nodeDeleted.getAdjIds().forEach(id -> Node.get(id).addAdjId(nodeDeleted.id)); //reconnect
        removedFrom.addNode(nodeDeleted);
        removedFrom.repaint();
    }

    @Override
    public void redo() {
        Node.removeNode(nodeDeleted.id);
        removedFrom.removeNode(nodeDeleted);
        removedFrom.repaint();
    }
    
    
}
