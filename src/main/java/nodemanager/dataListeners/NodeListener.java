package nodemanager.dataListeners;

import nodemanager.model.Graph;
import nodemanager.model.Node;

/**
 * Not implemented yet
 * @author Matt
 */
public interface NodeListener {
    public void nodeCreated(Node node, Graph in);
    public void nodeDeleted(Node node, Graph from);
}
