package nodemanager;

import java.awt.event.MouseEvent;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import nodemanager.gui.NodeManagerWindow;
import nodemanager.model.Graph;
import nodemanager.events.EventLog;
import nodemanager.gui.editPage.mapComponents.MapImage;
import nodemanager.modes.AbstractMode;
import nodemanager.modes.ModeNone;


/**
 * Moving towards using this as a controller
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */

public class NodeManager {
    private Graph graph;
    private AbstractMode mode;
    private final EventLog log;
    
    private static NodeManager instance;
    
    private NodeManager(){
        graph = Graph.createDefault();
        mode = new ModeNone();
        log = new EventLog();
    }
    
    public static final NodeManager getInstance(){
        if(instance == null){
            instance = new NodeManager();
        }
        return instance;
    }
    
    public final void setGraph(Graph g){
        this.graph = g;
    }
    
    public final Graph getGraph(){
        return graph;
    }
    
    public final void setMode(AbstractMode mode){
        if(mode == null){
            mode = new ModeNone();
        }
        this.mode = mode;
    }
    
    public final void mapClicked(MapImage map, MouseEvent me){
        if(this.mode != null){
            this.mode = this.mode.mapImageClicked(map, me);
        }
    }
    
    public final EventLog getLog(){
        return log;
    }
    
    public final void launchGui(){
        new NodeManagerWindow();
    }
    
    /**
     * NodeManager is the main class for the program
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        
        NodeManager.getInstance().launchGui();
    }
}
