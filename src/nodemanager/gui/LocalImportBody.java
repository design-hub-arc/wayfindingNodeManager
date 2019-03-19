package nodemanager.gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.nio.file.Files;
import java.util.HashMap;

/**
 * The LocalImportBody is used to 
 * import files on the user's computer 
 * into the program.
 * 
 * @author Matt Crow
 */
public class LocalImportBody extends Container implements ActionListener{
    private File folder;
    private final JButton selectFolder;
    private final JTextField selectedFolder;
    private final HashMap<String, FileCheckBox> fileCheckBoxes;
    private final HashMap<String, String> printToAbrev; 
    //the printed title of files to their abreviation on actual file names
    //move this to enum later.
    
    public LocalImportBody(){
        setLayout(new GridLayout(6, 1));
        
        fileCheckBoxes = new HashMap<>();
        printToAbrev = new HashMap<>();
        printToAbrev.put("node coordinates", "nodeCoord");
        printToAbrev.put("node connections", "nodeConn");
        printToAbrev.put("labels", "label");
        printToAbrev.put("map", "map");
        
        folder = null;
        
        selectFolder = new JButton("Select folder containing data");
        selectFolder.addActionListener(this);
        add(selectFolder);
        
        selectedFolder = new JTextField("no folder selected");
        selectedFolder.setEditable(false);
        add(selectedFolder);
        
        for(String type : printToAbrev.keySet()){
            fileCheckBoxes.put(printToAbrev.get(type), new FileCheckBox(type));
            add(fileCheckBoxes.get(printToAbrev.get(type)));
        }
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
        folder = f;
        selectedFolder.setText(f.getAbsolutePath());
        try {
            //find the most likely file that matches each type
            Files.list(f.toPath()).forEach((file)->{
                System.out.println(file.getFileName().toString());
                fileCheckBoxes.keySet().forEach((key)->{
                    System.out.println(key);
                    if(file.getFileName().toString().toUpperCase().contains(key.toUpperCase())){
                        fileCheckBoxes.get(key).selectFile(file.toFile());
                    }
                });
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
