package nodemanager.events;

import nodemanager.model.Graph;
import nodemanager.model.Node;

/**
 * Created whenever a label is removed from a node
 * @author Matt Crow
 */
public class LabelRemovedEvent extends EditEvent{
    private final Node removedFrom;
    private final String label;
    
    public LabelRemovedEvent(Graph g, Node n, String s){
        super(g);
        removedFrom = n;
        label = s;
    }
    
    @Override
    public void undoImpl(Graph g) {
        g.addLabel(label, removedFrom.getId());
    }
    @Override
    public void redoImpl(Graph g){
        g.removeLabel(label);
    }
}
