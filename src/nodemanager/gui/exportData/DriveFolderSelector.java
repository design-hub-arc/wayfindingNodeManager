package nodemanager.gui.exportData;

import java.awt.GridLayout;
import javax.swing.*;
import nodemanager.io.GoogleDriveUploader;

/**
 *
 * @author Matt Crow
 */
public class DriveFolderSelector extends JComponent{
    
    private final JTextField folderIdField;
    private final JTextArea msg;
    
    public DriveFolderSelector(){
        super();
        setLayout(new GridLayout(4, 1));
        
        folderIdField = new JTextField(GoogleDriveUploader.DEFAULT_FOLDER_ID);
        
        msg = new JTextArea();
        msg.setEditable(false);
        msg.setWrapStyleWord(true);
        msg.setLineWrap(true);
        
        JScrollPane j = new JScrollPane(msg);
        j.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        j.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        JButton verify = new JButton("Verify this folder works");
        verify.addActionListener((e)->{
            msg.setText("Hold on a second...");
            GoogleDriveUploader.isFolder(folderIdField.getText())
                .addOnFail((ex)->msg.setText("Hmm... looks like that file doesn't exist. Could you double check to make sure you have access?"))
                .addOnSucceed((bool)->{
                    if(bool){
                        msg.setText("Looks like that's a folder! Ready to upload!");
                    } else {
                        msg.setText("Nope, not a folder.");
                    }
                });
        });
        
        
        add(new JLabel("Select a folder to upload to"));
        add(folderIdField);
        add(j);
        add(verify);
    }
    
    public String getSelectedFolder(){
        return folderIdField.getText();
    }
}
