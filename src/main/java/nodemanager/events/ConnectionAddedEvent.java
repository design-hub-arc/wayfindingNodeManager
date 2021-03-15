package nodemanager.events;

import nodemanager.model.Graph;

/**
 * Created when a connection between nodes is added
 * @author Matt Crow
 */
public class ConnectionAddedEvent extends EditEvent{
    private final int id1;
    private final int id2;
    
    public ConnectionAddedEvent(Graph g, int from, int to){
        super(g);
        id1 = from;
        id2 = to;
    }
    
    @Override
    public void undoImpl(Graph g) {
        g.removeConnection(id1, id2);
    }

    @Override
    public void redoImpl(Graph g) {
        g.addConnection(id1, id2);
    }
    
}
