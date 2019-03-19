package nodemanager.gui;

import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
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
    private Consumer<File> func;
    
    public FileCheckBox(String text, String[] types, Consumer<File> fileConsumer){
        setLayout(new GridLayout(1, 3));
        
        func = fileConsumer;
        
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
                    types,
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
    
    public void importIfSelected(){
        if(include.isSelected()){
           func.accept(selectedFile);
        }
    }
}
