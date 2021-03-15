package nodemanager.events;

import nodemanager.model.Graph;

/**
 * This serves as the base class for the events used by the program.
 * Whenever the user uses this program to edit the data, we can create an instance of 
 * a subclass of Edit Event, then call 
 * <hr>
 * <code>
 * Session.logAction(event);
 * </code>
 * <hr>
 To record it. When overriding this class, 
 the constructor should take all the relevant data as parameters,
 undoImpl should revert the action,
 and redoImpl should do the action.
 * 
 * @author Matt Crow
 */
public abstract class EditEvent {
    private final Graph edittedGraph;
    
    public EditEvent(Graph edittedGraph){
        this.edittedGraph = edittedGraph;
    }
    
    public abstract void undoImpl(Graph g);
    public abstract void redoImpl(Graph g);
    
    public final void undo(){
        this.undoImpl(edittedGraph);
    }
    
    public final void redo(){
        this.redoImpl(edittedGraph);
    }
}
