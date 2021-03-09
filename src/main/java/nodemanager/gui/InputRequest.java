package nodemanager.gui;

import java.util.function.Consumer;

/**
 *
 * @author Matt
 */
public class InputRequest {
    private final String message;
    private final Consumer<String> doThisWithUserInput;
    
    public InputRequest(String message, Consumer<String> doThisWithUserInput){
        this.message = message;
        this.doThisWithUserInput = doThisWithUserInput;
    }
    
    public final String getMessage(){
        return message;
    }
    
    public final void accept(String userInput){
        doThisWithUserInput.accept(userInput);
    }
}
