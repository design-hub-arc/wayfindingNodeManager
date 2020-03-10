package nodemanager.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Provides a shorthand way to handle selection a file
 * @author Matt Crow
 */
public class FileSelector extends JMenuItem implements ActionListener{
    private final JFileChooser chooser;
    private final Consumer<File> action;
    
    public static final String[] CSV = new String[]{"Comma Separated Values", "csv"};
    public static final String[] DIR = new String[]{"Directory", "Folder"};
    public static final String[] IMAGE = new String[]{"png", "jpeg", "gif"};
    
    /**
     * Creates a FileSelector, which is a menu item that allows the user to select a file,
     * or a directory if they pass in JFileChooser.DIR as the types.
     * @param text the text to display on this component
     * @param types the file extentions this should allow
     * @param act a lambda expression to run when a file is chosen, passing in the file as a parameter
     */
    public FileSelector(String text, String[] types, Consumer<File> act){
        super(text);
        chooser = new JFileChooser();
        chooser.setFileSelectionMode((types == DIR)? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter(Arrays.toString(types), types));
        action = act;
        addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION){ 
            action.accept(chooser.getSelectedFile());
        }
    }
    
}
