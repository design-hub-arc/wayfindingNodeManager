package nodemanager.events;

import nodemanager.Session;
import nodemanager.model.Node;

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
        Session.getCurrentDataSet().removeLabel(label);
    }

    @Override
    public void redo() {
        Session.getCurrentDataSet().addLabel(label, addedTo.getId());
    }
}
