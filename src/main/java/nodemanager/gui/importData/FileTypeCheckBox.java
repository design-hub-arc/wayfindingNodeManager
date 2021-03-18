package nodemanager.gui.importData;

import nodemanager.files.AbstractWayfindingFile;
import nodemanager.files.FileType;

/**
 * Well this is kinda pointless
 * @author Matt Crow
 */
public class FileTypeCheckBox extends AbstractFileCheckbox{ 
    public FileTypeCheckBox(FileType t, AbstractWayfindingFile helper){
        super(t, helper);
    }
}
