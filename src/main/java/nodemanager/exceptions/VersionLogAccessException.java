package nodemanager.exceptions;

import nodemanager.io.GoogleDriveUploader;

/**
 *
 * @author Matt Crow
 */
public class VersionLogAccessException extends Exception{
    public VersionLogAccessException(){
        super("Please notify Matt (w1599227@apps.losrios.edu) that you need editting access to the version log");
        GoogleDriveUploader.deleteFileStore();
    }
}
