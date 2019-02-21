package nodemanager.events;

import nodemanager.node.Node;

/**
 * Created whenever a label is added to a node
 * @author Matt Crow
 */
public class LabelAddedEvent extends EditEvent{
    private final Node addedTo;
    private final String label;
    private final String labelType;
    
    public LabelAddedEvent(Node n, String s, String type){
        addedTo = n;
        label = s;
        labelType = type;
    }

    @Override
    public void undo() {
        addedTo.removeLabel(label);
    }

    @Override
    public void redo() {
        addedTo.addLabel(labelType, label);
    }
}
