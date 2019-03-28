package nodemanager.exceptions;

/**
 *
 * @author Matt Crow
 */
public class NoPermissionException extends Exception{
    public NoPermissionException(String fileId){
        super("It looks like you don't have access to the file " + fileId + ". Please log in using your student email. If that doesn't work, contact Matt (w1599227@apps.losrios.edu)");
    }
}
