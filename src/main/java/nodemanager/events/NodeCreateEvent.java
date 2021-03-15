package nodemanager.events;

import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.model.Graph;
import nodemanager.model.Node;

/**
 * Records when a node was created via the "add a node" button.
 * 
 * I can see this being problematic if it can create nodes with duplicate IDs
 * 
 * @author Matt Crow
 */
public class NodeCreateEvent extends EditEvent{
    private final Node nodeCreated;
    private final MapImage addedTo;
    
    /**
     * 
     * @param g
     * @param n the node that was created
     * @param m the mapimage that was clicked on to generate the event
     */
    public NodeCreateEvent(Graph g, Node n, MapImage m){
        super(g);
        nodeCreated = n;
        addedTo = m;
    }

    @Override
    public void undoImpl(Graph g) {
        addedTo.removeNode(nodeCreated);
        g.removeNode(nodeCreated.id);
    }

    @Override
    public void redoImpl(Graph g) {
        addedTo.addNode(nodeCreated);
        g.addNode(nodeCreated);
    }
}
