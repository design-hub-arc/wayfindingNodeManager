package nodemanager.modes;

/**
 *
 * @author Matt
 */
public class ModeRemoveConnection extends AbstractMode {
    private int nodeId;
    
    public ModeRemoveConnection(int nodeId) {
        super(String.format("Click on a node to disconnect it from node %d", nodeId));
        this.nodeId = nodeId;
    }

}
