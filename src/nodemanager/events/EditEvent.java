package nodemanager.events;

/**
 * This will serve as the base class for the events used by the program
 * @author W1599227
 */
public abstract class EditEvent {
    public abstract void undo();
    public abstract void redo();
}
