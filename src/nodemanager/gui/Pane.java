package nodemanager.gui;

import javax.swing.*;
import java.awt.*;

//literally just to space things
public class Pane extends JComponent{
    public Pane(){
        super();
        setLayout(new FlowLayout());
        setVisible(true);
    }
    /*
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, getWidth(), getHeight());
    }*/
}
