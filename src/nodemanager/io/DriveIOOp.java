package nodemanager.io;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Since interacting with the Google Drive may result in permission problems,
 * and threads can get a little weird with throwing errors, we need an interface to work with them
 * @author Matt Crow
 */
public abstract class DriveIOOp {
    private final ArrayList<Consumer<Exception>> onFail;
    private final ArrayList<Consumer<Object>> onSucceed;
    private Exception alreadyFailed; //just in case the thread is already done
    private Object alreadySucceeded;
    private Thread t;
    
    public DriveIOOp(){
        onFail = new ArrayList<>();
        onSucceed = new ArrayList<>();
        onFail.add((e)->e.printStackTrace());
    }
    
    public final Thread execute(){
        if(t == null){
            t = new Thread(){
                @Override
                public void run(){
                    try {
                        alreadySucceeded = perform();
                        onSucceed.forEach((func)->func.accept(alreadySucceeded));
                    } catch (Exception ex) {
                        alreadyFailed = ex;
                        onFail.forEach((func)->func.accept(ex));
                    }
                }
            };
            t.start();
        }
        return t;
    }
    
    public final DriveIOOp addOnSucceed(Consumer<Object> func){
        onSucceed.add(func);
        if(alreadySucceeded != null){
            func.accept(alreadySucceeded);
        }
        return this;
    }
    
    public final DriveIOOp addOnFail(Consumer<Exception> func){
        onFail.add(func);
        if(alreadyFailed != null){
            func.accept(alreadyFailed);
        }
        return this;
    }
    
    public abstract Object perform() throws Exception;
}
