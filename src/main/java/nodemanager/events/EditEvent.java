package nodemanager.events;

/**
 * This serves as the base class for the events used by the program.
 * Whenever the user uses this program to edit the data, we can create an instance of 
 * a subclass of Edit Event, then call 
 * <hr>
 * <code>
 * Session.logAction(event);
 * </code>
 * <hr>
 * To record it. When overriding this class, 
 * the constructor should take all the relevant data as parameters,
 * undo should revert the action,
 * and redo should do the action.
 * 
 * @author Matt Crow
 */
public abstract class EditEvent {
    public abstract void undo();
    public abstract void redo();
}
