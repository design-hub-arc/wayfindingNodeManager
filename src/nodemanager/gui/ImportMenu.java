package nodemanager.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This is used by EditCanvas to provide options for loading data into the program.
 * 
 * use this to remove code from EditCanvas
 * 
 * not sure how to make this work
 * 
 * @author Matt Crow
 */
public class ImportMenu extends JMenu{
    public ImportMenu(){
        super("Import");
        add(importNodeData());
    }
    
    private JMenuItem importNodeData(){
        JMenuItem jmi = new JMenuItem("Import Node Data");
        jmi.addActionListener((ActionEvent e) -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setFileFilter(new FileNameExtensionFilter("Node File", new String[]{"Comma Sperated Values", "csv"}));
            
            int response = jfc.showOpenDialog(jfc);
            if(response == JFileChooser.APPROVE_OPTION){
                
            }
        });
        return jmi;
    }
}
