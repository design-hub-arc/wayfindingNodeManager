package nodemanager.gui.importData;

import nodemanager.files.AbstractWayfindingFileHelper;
import nodemanager.files.FileType;

/**
 * Well this is kinda pointless
 * @author Matt Crow
 */
public class FileTypeCheckBox extends AbstractFileCheckbox{ 
    public FileTypeCheckBox(FileType t, AbstractWayfindingFileHelper helper){
        super(t, helper);
    }
}
