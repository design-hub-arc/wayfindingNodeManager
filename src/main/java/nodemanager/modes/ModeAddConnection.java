package nodemanager.modes;

/**
 *
 * @author Matt
 */
public class ModeAddConnection extends AbstractMode {
    private int nodeId;
    public ModeAddConnection(int id){
        super(String.format("Click on another node to connect it to node %d", id));
        this.nodeId = id;
    }
}
