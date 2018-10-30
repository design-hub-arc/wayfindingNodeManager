package nodemanager;

import nodemanager.node.Node;
import nodemanager.gui.NodeDataPane;

/*
The Session class is a static class used 
to store global variables used by the program.
*/
public class Session {
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
