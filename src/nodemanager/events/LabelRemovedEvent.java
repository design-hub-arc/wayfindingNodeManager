package nodemanager.events;

import nodemanager.node.Node;

/**
 * Created whenever a label is removed from a node
 * @author Matt Crow
 */
public class LabelRemovedEvent extends EditEvent{
    private final Node removedFrom;
    private final String label;
    private final String labelType;
    
    public LabelRemovedEvent(Node n, String s, String type){
        removedFrom = n;
        label = s;
        labelType = type;
    }
    
    @Override
    public void undo() {
        removedFrom.addLabel(labelType, label);
    }
    @Override
    public void redo(){
        removedFrom.removeLabel(label);
    }
}
