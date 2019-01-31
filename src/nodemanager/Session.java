package nodemanager;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import nodemanager.node.Node;
import nodemanager.gui.*;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */

public class Session {
    /**
    The Session class is a static class used 
    to store global variables used by the program.
    * 
    * While having global variables is generally not a good idea,
    * it is significantly better than passing a Session object to every object the program creates
    * 
    * Maybe make this extend JFrame?
    */
    public static Mode mode = Mode.NONE;
    public static Node selectedNode = null;
    public static NodeDataPane dataPane = null;
    public static int newMapX = 0;
    public static int newMapY = 0;
    public static int newMapWidth = 0;
    public static int newMapHeight = 0;
    public static JPanel currentPanel = null;

    /**
     * A text component used to display the program's controls
     */
    public static final JTextArea controlList = new JTextArea("Controls:\n");
    static{
        controlList.setEditable(false);
    }
    
    public static void selectNode(Node n){
        selectedNode = n;
        if(dataPane != null){
            dataPane.selectNode(n);
        }
    }
    
    /**
     * Adds a key control to the program.
     * 
     * For the run parameter, you can simply do
     * <br>
     * {@code
     * () -> {
     *     code to run when key is pressed
     * }
     * }
     * <br>
     * or
     * <br>
     * {@code
     * () -> code to run when key is pressed
     * }
     * <br>
     * it's that easy.
     * 
     * @param keyCode the keycode of the key to trigger the action. Use KeyEvent.VK_X to get the keycode
     * @param run the runnable to run whenever the given key is pressed
     * @param desc the description that will be displayed next to the key in the control list
     */
    public static void registerControl(int keyCode, Runnable run, String desc){
        KeyStroke key = KeyStroke.getKeyStroke(keyCode, 0);
        if(currentPanel == null){
            throw new NullPointerException("Must set currentWindow before registering controls!");
        }
        currentPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(key, key.toString());
        currentPanel.getActionMap().put(key.toString(), new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                run.run();
            }
        });
        
        controlList.append(key.toString() + ": " + desc + "\n");
    }
}
