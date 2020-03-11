package nodemanager.gui.importData;

import files.AbstractWayfindingFile;
import java.awt.GridLayout;
import java.io.File;
import javax.swing.*;
import nodemanager.gui.FileSelector;
import files.FileType;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Used by LocalImportBody to allow the user to choose what wayfinding files to import.
 * 
 * @author Matt Crow
 */
public class FileCheckBox extends AbstractFileCheckbox{
    private final JTextField fileName;
    private final JButton select;
    
    public FileCheckBox(FileType t){
        super(t);
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
                        try {
                            selectFile(f);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
            ).actionPerformed(e);
        });
        add(select);
    }
    
    public void selectFile(File f) throws IOException{
        AbstractWayfindingFile wayfindingFile = AbstractWayfindingFile.fromType(f.getName(), getFileType());
        wayfindingFile.setContents(new FileInputStream(f));
        super.selectFile(wayfindingFile);
        fileName.setText(f.getAbsolutePath());
    }
}
