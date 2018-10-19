package nodemanager.gui;

import javax.swing.JFrame;

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
