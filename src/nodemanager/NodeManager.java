package nodemanager;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import nodemanager.gui.Window;


/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */

public class NodeManager {
    /**
     * NodeManager is the main class for the program
     */
    public static void main(String[] args) {
        new Window();
        
        //pressing any key will cancel any operation in progress
        /*
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher(){
            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                Session.mode = Mode.NONE;
                return false;
            }
        });*/
    }
}
