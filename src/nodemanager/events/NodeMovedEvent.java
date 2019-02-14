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
    private int newX;
    private int newY;
    
    public NodeMovedEvent(Node n, int x, int y){
        moved = n;
        initialX = x;
        initialY = y;
        newX = x;
        newY = y;
    }
    
    @Override
    public void undo() {
        newX = moved.getIcon().getX();
        newY = moved.getIcon().getY();
        moved.getIcon().setPos(initialX, initialY);
        moved.getIcon().respositionNode(); //need to do this, or scaleTo will override the undo
        moved.getIcon().getHost().repaint();
    }

    @Override
    public void redo() {
        moved.getIcon().setPos(newX, newY);
        moved.getIcon().respositionNode();
        moved.getIcon().getHost().repaint();
    }
    
}
