package nodemanager.events;

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
        removedFrom.addLabel(label);
    }
    @Override
    public void redo(){
        removedFrom.removeLabel(label);
    }
}
