package nodemanager.io;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Since interacting with the Google Drive may result in permission problems,
 * and threads can get a little weird with throwing errors, we need an interface to work with them
 * 
 * A DriveIOOp allows us to keep track of an interaction with the Google Drive.
 * This interaction is defined in the perform() method. 
 * If perform() succeeds, passes the value returned from it to each onSucceed function
 * if it fails, passes the Exception to each onFail function.
 * 
 * Since DriveIOOps are not blocking, 
 * you have to put any code requiring the results of the operation
 * in an onSucceed function, or else wait until the operation's thread terminates
 * <div>
 * <h2> Examples </h2>
 * <hr>
 * <pre>{@code
 *  File f = GoogleDriveUploader.uploadFile(...);
 *  //do stuff with file
 * }</pre>
 * <b> BAD! </b>
 * <hr>
 * <pre>{@code
 *  GoogleDriveUploader.uploadFile(...).addOnSucceed((File f)->{
 *    //do stuff with file
 *  });
 * }</pre>
 * <b> GOOD! </b>
 * <hr>
 * <pre>{@code
 *  File localF = null;
 *  GoogleDriveUploader.uploadFile(...).addOnSucceed((File f)->{
 *    localF = f;
 *  }).getExecutingThread().join();
 *  //do stuff with localF
 * }</pre>
 * <b> OK! </b>
 * <hr>
 * </div>
 * @author Matt Crow
 * @param <T> The type returned by perform and fed into onSucceed
 */
public abstract class DriveIOOp <T>{
    private final ArrayList<Consumer<Exception>> onFail;
    private final ArrayList<Consumer<T>> onSucceed;
    private Exception alreadyFailed; //just in case the thread is already done
    private T alreadySucceeded;
    private final Thread t;
    
    public DriveIOOp(){
        onFail = new ArrayList<>();
        onSucceed = new ArrayList<>();
        onFail.add((e)->e.printStackTrace());
        
        t = new Thread(){
            @Override
            public void run(){
                try {
                    alreadySucceeded = perform();
                    onSucceed.forEach((func)->func.accept(alreadySucceeded));
                } catch (Exception ex) {
                    alreadyFailed = ex;
                    if(onFail.isEmpty()){
                        ex.printStackTrace();
                    }
                    onFail.forEach((func)->func.accept(ex));
                }
            }
        };
        t.start();
    }
    
    
    /**
     * @return the thread running this' perform() method
     */
    public final Thread getExcecutingThread(){
        return t;
    }
    
    /**
     * Adds a function which will accept the result of perform() if it succeeds.
     * If this had already succeeded, immediately passes the value of that success to the function
     * @param func a function to run upon successfully running perform()
     * @return this, for chaining purposes
     */
    public final DriveIOOp<T> addOnSucceed(Consumer<T> func){
        onSucceed.add(func);
        if(alreadySucceeded != null){
            func.accept(alreadySucceeded);
        }
        return this;
    }
    
    /**
     * Adds a function which will accept the result of perform() if it fails.
     * If this had already failed, immediately passes the value of that failure to the function
     * @param func a function to run upon successfully running perform()
     * @return this, for chaining purposes
     */
    public final DriveIOOp<T> addOnFail(Consumer<Exception> func){
        onFail.add(func);
        if(alreadyFailed != null){
            func.accept(alreadyFailed);
        }
        return this;
    }
    
    /**
     * DriveIOOp runs the contents of method asynchronously upon calling .execute()
     * @return what you want to pass to success functions
     * @throws Exception what you want to pass to failure functions
     */
    public abstract T perform() throws Exception;
}
