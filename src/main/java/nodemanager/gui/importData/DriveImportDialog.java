package nodemanager.gui.importData;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 * Serves as the base for the import dialog box
 * @author Matt Crow
 */
public class DriveImportDialog extends JDialog{
    public DriveImportDialog(Frame f){
        super(f, "Import");
        setContentPane(new DriveImportBody());
        pack();
        setVisible(true);
    }
}
