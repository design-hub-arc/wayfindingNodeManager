package nodemanager;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import nodemanager.gui.NodeManagerWindow;
import nodemanager.model.Graph;


/**
 * Moving towards using this as a controller
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */

public class NodeManager {
    private Graph graph;
    
    private static NodeManager instance;
    
    private NodeManager(){
        graph = Graph.createDefault();
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
