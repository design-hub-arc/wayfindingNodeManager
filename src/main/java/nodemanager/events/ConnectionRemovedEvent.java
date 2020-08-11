package nodemanager.events;

import nodemanager.node.Node;

/**
 * created when a node connection is broken
 * @author Matt Crow
 */
public class ConnectionRemovedEvent extends EditEvent{
    private final int id1;
    private final int id2;
    
    public ConnectionRemovedEvent(int from, int to){
        id1 = from;
        id2 = to;
    }
    
    @Override
    public void undo() {
        Node.get(id1).addAdjId(id2);
    }
    
    @Override
    public void redo(){
        Node.get(id1).removeAdj(id2);
    }
}
