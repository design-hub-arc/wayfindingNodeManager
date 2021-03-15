package nodemanager.events;

import nodemanager.Session;
import nodemanager.model.Graph;
import nodemanager.model.Node;

/**
 * Created whenever a label is added to a node
 * @author Matt Crow
 */
public class LabelAddedEvent extends EditEvent{
    private final Node addedTo;
    private final String label;
    
    public LabelAddedEvent(Graph g, Node n, String s){
        super(g);
        addedTo = n;
        label = s;
    }

    @Override
    public void undoImpl(Graph g) {
        g.removeLabel(label);
    }

    @Override
    public void redoImpl(Graph g) {
        g.addLabel(label, addedTo.getId());
    }
}
