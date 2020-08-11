package nodemanager.gui.importData;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 *
 * @author Matt Crow
 */
public class LocalImportDialog extends JDialog{
    public LocalImportDialog(Frame f){
        super(f, "Import Local Files");
        setContentPane(new LocalImportBody());
        pack();
        setVisible(true);
    }
}
