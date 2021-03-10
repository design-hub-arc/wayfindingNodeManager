package nodemanager;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import nodemanager.gui.NodeManagerWindow;


/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */

public class NodeManager {
    /**
     * NodeManager is the main class for the program
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        new NodeManagerWindow();
    }
}
