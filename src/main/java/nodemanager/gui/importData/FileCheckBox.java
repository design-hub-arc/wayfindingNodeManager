package nodemanager.gui.importData;

import nodemanager.files.AbstractWayfindingFileHelper;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.*;
import nodemanager.gui.FileSelector;
import nodemanager.files.FileType;

/**
 * Used by LocalImportBody to allow the user to choose what wayfinding files to import.
 * 
 * @author Matt Crow
 */
public class FileCheckBox extends AbstractFileCheckbox{
    private final JTextField fileName;
    private final JButton select;
    
    public FileCheckBox(FileType t, AbstractWayfindingFileHelper fileHelper){
        super(t, fileHelper);
        setLayout(new GridLayout(1, 3));
        
        fileName = new JTextField("no " + getFileType().getTitle() + " file selected");
        fileName.setEditable(false);
        add(fileName);
        
        select = new JButton("Select a different file");
        select.addActionListener((e)->{
            new FileSelector(
                    "Select " + getFileType().getTitle() + " file",
                    new String[]{t.getFileExtention()},
                    (f)->{
                        selectFile(f);
                    }
            ).actionPerformed(e);
        });
        add(select);
    }
    
    @Override
    public void selectFile(File f) {
        super.selectFile(f);
        fileName.setText(f.getAbsolutePath());
    }
}
