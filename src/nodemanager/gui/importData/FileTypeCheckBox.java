package nodemanager.gui.importData;

import java.awt.GridLayout;
import javax.swing.*;
import nodemanager.io.FileType;

/**
 *
 * @author Matt Crow
 */
public class FileTypeCheckBox extends JComponent{
    private FileType type;
    private JCheckBox include;
    
    public FileTypeCheckBox(FileType t){
        super();
        type = t;
        setLayout(new GridLayout(1, 2));
        include = new JCheckBox("Include " + type.getTitle() + " file", true);
        add(include);
        
        JTextField title = new JTextField(type.getTitle());
        title.setEditable(false);
        add(title);
        
        include.addActionListener((e)->{
            title.setEnabled(include.isSelected());
        });
    }
    
    public FileType getType(){
        return type;
    }
    
    public boolean getIncludes(){
        return include.isSelected();
    }
}
