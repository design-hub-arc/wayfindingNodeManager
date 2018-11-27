package nodemanager.gui;

import javax.swing.*;
import java.awt.*;

/**
 * A Horizontal version of MenuBar.
 * @see MenuBar
 * @author Matt Crow
 */
public class Sidebar extends JComponent{

    /**
     * Adds a component to this element, then sets the layout to evenly distribute space among each element
     * @param c the Component to add
     * @return the added Component
     */
    @Override
    public Component add(Component c){
        super.add(c);
        setLayout(new GridLayout(getComponentCount(), 1));
        revalidate();
        repaint();
        return c;
    }
}
