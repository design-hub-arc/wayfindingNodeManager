package nodemanager.gui;

import java.awt.GridLayout;
import java.io.File;
import javax.swing.*;

/**
 * Used by LocalImportBody to allow the user to choose what files to import
 * @author Matt Crow
 */
public class FileCheckBox extends JComponent{
    private final JCheckBox include;
    private final JTextField fileName;
    private final JButton select;
    private File selectedFile;
    
    public FileCheckBox(String text){
        setLayout(new GridLayout(1, 3));
        
        selectedFile = null;
        
        include = new JCheckBox("Include " + text + " file", true);
        add(include);
        
        fileName = new JTextField("no " + text + " file selected");
        fileName.setEditable(false);
        add(fileName);
        
        select = new JButton("Select a different file");
        select.addActionListener((e)->{
            new FileSelector(
                    "Select " + text + " file",
                    FileSelector.CSV,
                    (f)->{
                        selectFile(f);
                    }
            ).actionPerformed(e);
        });
        add(select);
        
        include.addActionListener((e)->{
            fileName.setEnabled(include.isSelected());
            select.setEnabled(include.isSelected());
        });
    }
    
    public void selectFile(File f){
        selectedFile = f;
        fileName.setText(f.getAbsolutePath());
    }
}
