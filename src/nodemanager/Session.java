package nodemanager;

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
    public static JTextArea controlList = new JTextArea("Controls:\n");
    {
        controlList.setEditable(false);
    }
    
    public static void selectNode(Node n){
        selectedNode = n;
        if(dataPane != null){
            dataPane.selectNode(n);
        }
    }
    
    public static void registerControl(KeyStroke key, Action a, String desc){
        if(currentPanel == null){
            throw new NullPointerException("Must set currentWindow before registering controls!");
        }
        currentPanel.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(key, key.toString());
        currentPanel.getActionMap().put(key.toString(), a);
        
        controlList.append(key.toString() + ": " + desc + "\n");
    }
}
