package nodemanager.gui;

import javax.swing.JFrame;

/**
 * Basic JFrame used to host the EditCanvas
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class Window extends JFrame{
    public Window(){
        super();
        setTitle("Wayfinding Node Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(new EditCanvas());
        pack();
        setVisible(true);
    }
}
