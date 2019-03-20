package nodemanager.gui.importData;

import java.awt.GridLayout;
import java.io.File;
import javax.swing.*;
import nodemanager.gui.FileSelector;
import nodemanager.io.Importer;
import nodemanager.io.FileType;

/**
 * Used by LocalImportBody to allow the user to choose what wayfinding files to import.
 * 
 * @author Matt Crow
 */
public class FileCheckBox extends JComponent{
    private final JCheckBox include;
    private final JTextField fileName;
    private final JButton select;
    private File selectedFile;
    private FileType type;
    
    public FileCheckBox(FileType t){
        setLayout(new GridLayout(1, 3));
        
        type = t;
        
        selectedFile = null;
        
        include = new JCheckBox("Include " + type.getTitle() + " file", true);
        add(include);
        
        fileName = new JTextField("no " + type.getTitle() + " file selected");
        fileName.setEditable(false);
        add(fileName);
        
        select = new JButton("Select a different file");
        select.addActionListener((e)->{
            new FileSelector(
                    "Select " + type.getTitle() + " file",
                    new String[]{t.getFileExtention()},
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
    
    public FileType getFileType(){
        return type;
    }
    
    public void selectFile(File f){
        selectedFile = f;
        fileName.setText(f.getAbsolutePath());
    }
    
    public void importIfSelected(){
        if(include.isSelected()){
            Importer.importFile(selectedFile, type);
        }
    }
}
