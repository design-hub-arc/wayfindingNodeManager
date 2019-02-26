package nodemanager.events;

import nodemanager.node.Node;

/**
 * Created whenever a label is added to a node
 * @author Matt Crow
 */
public class LabelAddedEvent extends EditEvent{
    private final Node addedTo;
    private final String label;
    
    public LabelAddedEvent(Node n, String s){
        addedTo = n;
        label = s;
    }

    @Override
    public void undo() {
        addedTo.removeLabel(label);
    }

    @Override
    public void redo() {
        addedTo.addLabel(label);
    }
}
