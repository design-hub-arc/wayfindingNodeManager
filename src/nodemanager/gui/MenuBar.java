package nodemanager.gui;

import javax.swing.*;
import java.awt.*;

public class MenuBar extends JComponent{
    @Override
    public Component add(Component c){
        super.add(c);
        setLayout(new GridLayout(1, getComponentCount()));
        revalidate();
        repaint();
        return c;
    }
}
