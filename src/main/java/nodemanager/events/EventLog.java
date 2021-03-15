package nodemanager.events;

import java.util.Stack;

/**
 *
 * @author Matt
 */
public class EventLog {
    private final Stack<EditEvent> log;
    private final Stack<EditEvent> undone;
    
    public EventLog(){
        log = new Stack<>();
        undone = new Stack<>();
    }
    
    public final void log(EditEvent e){
        log.push(e);
    }
    
    public final void undo(){
        if(!log.isEmpty()){
            undone.push(log.pop()).undo();
        }
    }
    
    public final void redo(){
        if(!undone.isEmpty()){
            log.push(undone.pop()).redo();
        }
    }
    
    public final boolean isSaved(){
        // clear is called after saving
        return log.isEmpty();
    }
    
    public final void clear(){
        log.clear();
        undone.clear();
    }
}
