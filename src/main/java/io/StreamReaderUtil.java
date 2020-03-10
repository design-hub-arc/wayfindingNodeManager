package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Matt
 */
public class StreamReaderUtil {
    public static String readStream(InputStream in) throws IOException{
        StringBuilder ret = new StringBuilder();
        InputStreamReader read = new InputStreamReader(in);
        BufferedReader buff = new BufferedReader(read);
        while(buff.ready()){
            ret.append(buff.readLine()).append('\n');
        }
        return ret.toString();
    }
}
