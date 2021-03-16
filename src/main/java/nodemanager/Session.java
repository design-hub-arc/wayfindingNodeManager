package nodemanager;

import nodemanager.gui.editPage.NodeDataPane;
import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.modes.ModeRescaleUpperLeft;
import java.util.ArrayList;
import nodemanager.events.EditEvent;
import nodemanager.model.Node;
import nodemanager.gui.*;
import nodemanager.modes.AbstractMode;
import nodemanager.modes.ModeAddConnection;
import nodemanager.modes.ModeMove;
import nodemanager.modes.ModeNewNode;
import nodemanager.modes.ModeNone;
import nodemanager.modes.ModeRemoveConnection;
import nodemanager.modes.ModeRescaleLowerRight;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */


/**
* The Session class is a static class used 
* to store global variables used by the program.
* 
* While having global variables is generally not a good idea,
* it is significantly better than passing a Session object to every object the program creates
*/
public class Session {
    private static Mode mode = Mode.NONE;
    private static AbstractMode newMode = new ModeNone();
    public static Node selectedNode = null;
    public static NodeDataPane dataPane = null;
    public static int newMapX = 0;
    public static int newMapY = 0;
    public static int newMapWidth = 0;
    public static int newMapHeight = 0;
    
    // the view
    public static MapImage map = null;
    
    public static void selectNode(Node n){
        selectedNode = n;
        if(dataPane != null){
            dataPane.selectNode(n);
        }
    }
    
    public static void setMode(Mode m){
        mode = m;
        switch(m){
            case ADD_CONNECTION:
                newMode = new ModeAddConnection(selectedNode.id);
                break;
            case REMOVE_CONNECTION:
                newMode = new ModeRemoveConnection(selectedNode.id);
                break;
            case RESCALE_UL:
                newMode = new ModeRescaleUpperLeft();
                break;
            case RESCALE_LR:
                newMode = new ModeRescaleLowerRight();
                break;
            default:
                newMode = new ModeNone();
                break;
        }
        InputConsole.getInstance().writeMessage(String.format("Current mode: \n* %s\n(%s)", m.toString(), newMode.getMessage()));
    }
    
    public final static Mode getMode(){
        return mode;
    }
}
