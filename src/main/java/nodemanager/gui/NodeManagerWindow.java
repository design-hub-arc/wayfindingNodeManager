package nodemanager.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import nodemanager.Session;

/**
 * Basic JFrame used to host the EditCanvas
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class NodeManagerWindow extends JFrame{
    private final ApplicationBody body;
    
    public NodeManagerWindow(){
        super();
        setTitle("Wayfinding Node Manager");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
               
        JFrame self = this;
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent we) {
                boolean close = Session.isSaved || JOptionPane.showConfirmDialog(
                        self, 
                        "You might have unsaved work, are you sure you want to exit?", 
                        "Warning", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                ) == JOptionPane.YES_OPTION;
                if(close){
                    self.dispose();
                    System.exit(0);
                }
            }

            @Override
            public void windowClosed(WindowEvent we) {}
        });
        body = new ApplicationBody();
        
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(body);
        setJMenuBar(new ApplicationMenuBar(this));
        pack();
        setVisible(true);
    }
    
    public final ApplicationBody getBody(){
        return body;
    }
}
