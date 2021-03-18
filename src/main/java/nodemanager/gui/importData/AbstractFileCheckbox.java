package nodemanager.gui.importData;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.*;
import nodemanager.files.AbstractWayfindingFile;
import nodemanager.files.FileType;
import nodemanager.model.Graph;

/**
 * This will serve as the base for local- and drive import file choosers.
 * 
 * Will display a check box, which is used to decide if a given file type should be imported.
 * 
 * @author Matt Crow
 */
public abstract class AbstractFileCheckbox extends JComponent{
    private final FileType fileType;
    private final JCheckBox include;
    private final AbstractWayfindingFile fileHelper;
    private File selectedFile;

    /**
     *
     * @param t What type of file this allows the user to select
     * @param fileHelper the Object to help this import or export files
     */
    public AbstractFileCheckbox(FileType t, AbstractWayfindingFile fileHelper){
        super();
        setLayout(new GridLayout(1, 1));
        fileType = t;
        include = new JCheckBox("Include " + t.getTitle() + " file", true);
        selectedFile = null;
        add(include);
        this.fileHelper = fileHelper;
    }
    
    /**
     * 
     * @return the wayfinding file type this allows the user to select
     */
    public final FileType getFileType(){
        return fileType;
    }
    
    /**
     * Sets which file will be imported when this.importIfSelected() is called.
     * the file can be either from the local system, or the drive.
     * What happens when it is imported is determined based on what this' file type is.
     * @param f 
     */
    public void selectFile(File f){
        selectedFile = f;
    }
    
    public final boolean isSelected(){
        return include.isSelected();
    }
    
    public final void importIfSelected(Graph g){
        if(selectedFile != null && include.isSelected()){
            try {
                this.fileHelper.readGraphDataFromFile(g, new FileInputStream(selectedFile));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
