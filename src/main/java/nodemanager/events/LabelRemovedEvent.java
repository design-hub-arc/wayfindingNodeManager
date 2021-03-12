package nodemanager.events;

import nodemanager.Session;
import nodemanager.node.Node;

/**
 * Created whenever a label is removed from a node
 * @author Matt Crow
 */
public class LabelRemovedEvent extends EditEvent{
    private final Node removedFrom;
    private final String label;
    
    public LabelRemovedEvent(Node n, String s){
        removedFrom = n;
        label = s;
    }
    
    @Override
    public void undo() {
        Session.getCurrentDataSet().addLabel(label, removedFrom.getId());
    }
    @Override
    public void redo(){
        Session.getCurrentDataSet().removeLabel(label);
    }
}
