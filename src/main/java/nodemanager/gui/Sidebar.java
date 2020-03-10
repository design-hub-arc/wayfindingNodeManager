package nodemanager.gui;

import javax.swing.*;
import java.awt.*;

/**
 * A Horizontal version of MenuBar.
 * @see MenuBar
 * @author Matt Crow
 */
public class Sidebar extends JComponent{
    private GridBagConstraints gbc;
    
    public Sidebar(){
        super();
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1.0;
    }
    
    /**
     * Adds a component to this element, then sets the layout to evenly distribute space among each element
     * @param c the Component to add
     * @return the added Component
     */
    @Override
    public Component add(Component c){
        super.add(c, gbc);
        gbc.gridy++;
        revalidate();
        repaint();
        return c;
    }
}
