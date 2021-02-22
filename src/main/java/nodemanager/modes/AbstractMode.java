package nodemanager.modes;

/**
 *
 * @author Matt Crow
 */
public abstract class AbstractMode {
    private final String message;
    
    public AbstractMode(String message){
        this.message = message;
    }
    
    public final String getMessage(){
        return message;
    }
}
