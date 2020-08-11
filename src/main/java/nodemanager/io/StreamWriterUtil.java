package nodemanager.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author Matt
 */
public class StreamWriterUtil {
    public static void writeStream(OutputStream out, String content) throws IOException{
        OutputStreamWriter write = new OutputStreamWriter(out);
        try (BufferedWriter buff = new BufferedWriter(write)) {
            buff.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex; //make sure to report the error
        }
    }
}
