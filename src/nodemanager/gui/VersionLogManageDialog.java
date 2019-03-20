package nodemanager.gui;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * The base of the popup allowing users
 * to manually manage the version log.
 * @author Matt Crow
 */
public class VersionLogManageDialog extends JDialog{
    public VersionLogManageDialog(Frame f){
        super(f, "Export");
        setContentPane(new VersionLogBody());
        pack();
        setVisible(true);
    }
    
    public static void main(String[] args){
        JFrame f = new JFrame();
        f.setSize(500, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        new VersionLogManageDialog(f);
        f.setVisible(true);
    }
}
