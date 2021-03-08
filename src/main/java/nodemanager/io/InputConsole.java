package nodemanager.io;

import java.util.function.Consumer;
import javax.swing.JOptionPane;

/**
 * This will eventually be a GUI component to replace JOptionPane
 * @author Matt
 */
public class InputConsole {
    private static InputConsole instance;
    
    private InputConsole(){
        
    }
    
    public static InputConsole getInstance(){
        if(instance == null){
            instance = new InputConsole();
        }
        return instance;
    }
    
    public final void warn(String message){
        JOptionPane.showMessageDialog(null, message);
    }
    
    public final void askString(String message, Consumer<String> then){
        String response = JOptionPane.showInputDialog(message);
        then.accept(response);
    }
    
    // need to change to non-blocking once I implement into GUI
    public final void askInt(String message, Consumer<Integer> then){
        String response = JOptionPane.showInputDialog(message);
        try {
            int asInt = Integer.parseInt(response);
            then.accept(asInt);
        } catch(NumberFormatException ex){
            warn(String.format("I couldn't find a number in %s", response));
        }
    }
}
