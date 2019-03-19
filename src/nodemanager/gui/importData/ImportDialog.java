package nodemanager.gui.importData;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 * Serves as the base for the import dialog box
 * @author Matt Crow
 */
public class ImportDialog extends JDialog{
    public ImportDialog(Frame f){
        super(f, "Import");
        setContentPane(new ImportBody());
        pack();
        setVisible(true);
    }
}
