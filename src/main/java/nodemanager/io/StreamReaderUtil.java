package nodemanager.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Matt
 */
public class StreamReaderUtil {
    public static final char NEWLINE = '\n';
    
    public static String readStream(InputStream in) throws IOException{
        StringBuilder ret = new StringBuilder();
        InputStreamReader read = new InputStreamReader(in);
        try (BufferedReader buff = new BufferedReader(read)) {
            while(buff.ready()){
                ret.append(buff.readLine()).append(NEWLINE);
            }
        } catch (IOException ex){
            ex.printStackTrace();
            throw ex; //make sure the calling method knows an error occured
        }
        return ret.toString();
    }
}
