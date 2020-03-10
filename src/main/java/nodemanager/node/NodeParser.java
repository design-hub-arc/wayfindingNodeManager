package nodemanager.node;

import java.io.*;
import static java.lang.System.out;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * The NodeParser is a static class used to create Nodes from an InputStream.
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class NodeParser {
    /**
     * Reads each line from an InputStream, 
     * feeding each of them (split on comma),
     * one by one into the parser interface.
     * 
     * @param s an InputStream from a csv file
     * @param parser an instance of the parser interface, 
     * which takes an array of strings as a parameter.
     * You can do this shorthand using
     * <hr>
     * {@code
     *  parseFile(stream, (line) -> {
     *   do stuff with line
     *  });
     * }
     */
    public static void parseFile(InputStream s, Consumer<String[]> parser){
        BufferedReader br;
        String[] line = new String[0];
        boolean firstLine = true;
        try{
            br = new BufferedReader(new InputStreamReader(s));
            while(br.ready()){
                try{
                    line = br.readLine().split(",");
                    parser.accept(line);
                } catch(Exception e){
                    if(!firstLine){
                        //don't print errors for first line, as it will always fail, being a header
                        out.println("Line fail: " + Arrays.toString(line));
                        e.printStackTrace();
                    }
                }
                firstLine = false;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
