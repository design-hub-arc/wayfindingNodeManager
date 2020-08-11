package nodemanager.gui.exportData;

import java.awt.Frame;
import javax.swing.JDialog;

/**
 * Serves as the base for the export dialog box.
 * @author matt
 */
public class ExportDialog extends JDialog{
    public ExportDialog(Frame f){
        super(f, "Export");
        setContentPane(new ExportBody());
        pack();
        setVisible(true);
    }
}
