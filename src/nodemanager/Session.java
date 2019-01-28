package nodemanager;

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
    */
    public static Mode mode = Mode.NONE;
    public static Node selectedNode = null;
    public static NodeDataPane dataPane = null;
    public static int newMapX = 0;
    public static int newMapY = 0;
    public static int newMapWidth = 0;
    public static int newMapHeight = 0;
    
    public static void selectNode(Node n){
        selectedNode = n;
        if(dataPane != null){
            dataPane.selectNode(n);
        }
    }
}
