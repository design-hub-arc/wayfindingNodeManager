package nodemanager.events;

import nodemanager.node.Node;

/**
 * Records when a node is moved via the "move this node" button
 * @author Matt Crow
 */
public class NodeMovedEvent extends EditEvent{
    private final Node moved;
    private final int initialX;
    private final int initialY;
    
    public NodeMovedEvent(Node n, int x, int y){
        moved = n;
        initialX = x;
        initialY = y;
    }
    
    @Override
    public void undo() {
        moved.getIcon().setPos(initialX, initialY);
        moved.getIcon().respositionNode(); //need to do this, or scaleTo will override the undo
        moved.getIcon().getHost().repaint();
    }
    
}
