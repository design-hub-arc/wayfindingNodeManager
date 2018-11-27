package nodemanager.gui;

import javax.swing.*;
import java.awt.*;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 * 
 * Used to position components in a horizontal row
 */
public class MenuBar extends JComponent{
    /**
     * Adds a component to this element, then sets the layout to evenly distribute space among each element
     * @param c the Component to add
     * @return the added Component
     */
    @Override
    public Component add(Component c){
        super.add(c);
        setLayout(new GridLayout(1, getComponentCount()));
        revalidate();
        repaint();
        return c;
    }
}
