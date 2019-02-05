package nodemanager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Stack;
import javax.swing.*;
import nodemanager.events.EditEvent;
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
    private static Mode mode = Mode.NONE;
    public static Node selectedNode = null;
    public static NodeDataPane dataPane = null;
    public static int newMapX = 0;
    public static int newMapY = 0;
    public static int newMapWidth = 0;
    public static int newMapHeight = 0;
    public static JPanel currentPanel = null;
    
    //used to undo actions
    private static final Stack<EditEvent> ACTIONS = new Stack<>();

    /**
     * A text component used to display the program's controls
     */
    public static final JTextArea CONTROL_LIST = new JTextArea("Controls:\n");
    
    public static final JLabel MODE_LABEL = new JLabel("Current mode: " + Mode.NONE.toString());
    
    static{
        CONTROL_LIST.setEditable(false);
        MODE_LABEL.setMaximumSize(new Dimension(100, 30));
        MODE_LABEL.setBackground(Color.GRAY);
        MODE_LABEL.setOpaque(true);
        MODE_LABEL.revalidate();
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
        
        CONTROL_LIST.append(key.toString() + ": " + desc + "\n");
    }
    
    public static void setMode(Mode m){
        mode = m;
        MODE_LABEL.setText("Current mode: " + m.toString());
    }
    
    public static Mode getMode(){
        return mode;
    }
    
    public static void logAction(EditEvent e){
        ACTIONS.add(e);
    }
    
    public static void undoLastAction(){
        if(!ACTIONS.isEmpty()){
            EditEvent e = ACTIONS.pop();
            System.out.println("Undoing " + e.toString());
            e.undo();
        }
    }
}
