package nodemanager.events;

import nodemanager.gui.MapImage;
import nodemanager.node.Node;

/**
 * Records when a node was created via the "add a node" button.
 * @author Matt Crow
 */
public class NodeCreateEvent extends EditEvent{
    private final Node nodeCreated;
    private final MapImage addedTo;
    
    /**
     * 
     * @param n the node that was created
     * @param m the mapimage that was clicked on to generate the event
     */
    public NodeCreateEvent(Node n, MapImage m){
        nodeCreated = n;
        addedTo = m;
    }

    @Override
    public void undo() {
        addedTo.removeNode(nodeCreated);
        Node.removeNode(nodeCreated.id);
    }

    @Override
    public void redo() {
        addedTo.addNode(nodeCreated);
        Node.addNode(nodeCreated);
    }
}
