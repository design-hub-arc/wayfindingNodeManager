package nodemanager.gui;

import javax.swing.*;
import java.awt.*;

public class Sidebar extends JComponent{
    @Override
    public Component add(Component c){
        super.add(c);
        setLayout(new GridLayout(getComponentCount(), 1));
        revalidate();
        repaint();
        return c;
    }
}
