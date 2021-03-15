package nodemanager.events;

import nodemanager.Session;
import nodemanager.gui.editPage.mapComponents.NodeIcon;
import nodemanager.model.Graph;
import nodemanager.model.Node;

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
    
    public NodeMovedEvent(Graph g, Node n, int x, int y){
        super(g);
        moved = n;
        initialX = x;
        initialY = y;
        newX = x;
        newY = y;
    }
    
    @Override
    public void undoImpl(Graph g) {
        NodeIcon icon = Session.map.getIcon(moved.getId());
        newX = icon.getX();
        newY = icon.getY();
        icon.setPos(initialX, initialY);
        moved.setX((int)icon.getScale().inverseX(icon.getX()));
        moved.setY((int)icon.getScale().inverseY(icon.getY()));
        icon.getHost().repaint();
    }

    @Override
    public void redoImpl(Graph g) {
        NodeIcon icon = Session.map.getIcon(moved.getId());
        icon.setPos(newX, newY);
        moved.setX((int)icon.getScale().inverseX(icon.getX()));
        moved.setY((int)icon.getScale().inverseY(icon.getY()));
        icon.getHost().repaint();
    }
    
}
