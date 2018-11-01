package nodemanager;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import nodemanager.gui.Window;

public class NodeManager {

    public static void main(String[] args) {
        new Window();
        
        //pressing escape will cancel any operation in progress
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher(){
            @Override
            public boolean dispatchKeyEvent(KeyEvent ke) {
                Session.mode = Mode.NONE;
                return false;
            }
        });
    }
    
}
