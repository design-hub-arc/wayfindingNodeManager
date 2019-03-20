package nodemanager.gui.importData;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.nio.file.Files;
import java.util.ArrayList;
import nodemanager.gui.FileSelector;
import nodemanager.io.*;

/**
 * The LocalImportBody is used to 
 * import files on the user's computer 
 * into the program.
 * 
 * @author Matt Crow
 */
public class LocalImportBody extends Container implements ActionListener{
    private final JButton selectFolder;
    private final JTextField selectedFolder;
    private final ArrayList<FileCheckBox> fileCheckBoxes;
    
    public LocalImportBody(){
        setLayout(new GridLayout(7, 1));
        
        fileCheckBoxes = new ArrayList<>();
        
        selectFolder = new JButton("Select folder containing data");
        selectFolder.addActionListener(this);
        add(selectFolder);
        
        selectedFolder = new JTextField("no folder selected");
        selectedFolder.setEditable(false);
        add(selectedFolder);
        
        fileCheckBoxes.add( 
                new FileCheckBox(
                        FileType.NODE_COORD
                )
        );
        fileCheckBoxes.add( 
                new FileCheckBox(
                        FileType.NODE_CONN
                )
        );
        fileCheckBoxes.add( 
                new FileCheckBox(
                        FileType.LABEL
                )
        );
        fileCheckBoxes.add( 
                new FileCheckBox(
                        FileType.MAP_IMAGE
                )
        );
        fileCheckBoxes.forEach((box)->add(box));
        
        JButton importAll = new JButton("Import the selected files");
        importAll.addActionListener((e)->{
            for(FileCheckBox b : fileCheckBoxes){
                //don't do lambda. Needs to do in order.
                b.importIfSelected();
            }
        });
        add(importAll);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        new FileSelector(
                "Select a directory",
                FileSelector.DIR,
                (f)->{
                    selectFolder(f);
                }
        ).actionPerformed(ae);
    }
    
    private void selectFolder(File f){
        selectedFolder.setText(f.getAbsolutePath());
        try {
            //find the most likely file that matches each type
            Files.list(f.toPath()).forEach((file)->{
                fileCheckBoxes.forEach((checkBox)->{
                    if(file.getFileName().toString().toUpperCase().contains(checkBox.getFileType().getSuffix().toUpperCase())){
                        checkBox.selectFile(file.toFile());
                    }
                });
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
