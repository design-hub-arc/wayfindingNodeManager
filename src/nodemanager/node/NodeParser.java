package nodemanager.node;

import java.io.*;
import static java.lang.System.out;
import java.util.Arrays;

/**
 * The NodeParser is a static class used to create Nodes from an InputStream.
 * 
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class NodeParser {
    
    /**
     * Generates Nodes from an InputStream coming from a file
     * @param s an InputStream of a Node csv file
     */
    public static void parseNodeFile(InputStream s){
        BufferedReader br;
        String[] line;
        int id;
        int x;
        int y;
        
        try{
            br = new BufferedReader(new InputStreamReader(s));
            while(br.ready()){
                line = br.readLine().split(",");
                try{
                    id = Integer.parseInt(line[0].trim());
                    x = Integer.parseInt(line[1].trim());
                    y = Integer.parseInt(line[2].trim());
                    new Node(id, x, y);
                } catch(NumberFormatException e){
                    out.println("Line fail: " + Arrays.toString(line));
                }
            }
            br.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Reads Node connection data from an InputStream generated by a csv file.
     * @param s an InputStream of a csv file generated by the program.
     */
    public static void parseConnFile(InputStream s){
        BufferedReader br;
        String[] line;
        int id;
        int adj;
        
        try{
            br = new BufferedReader(new InputStreamReader(s));
            while(br.ready()){
                line = br.readLine().split(",");
                try{
                    id = Integer.parseInt(line[0].trim());
                    adj = Integer.parseInt(line[1].trim());
                    Node.get(id).addAdjId(adj);
                } catch(NumberFormatException e){
                    out.println("Line fail: " + Arrays.toString(line));
                } catch(NullPointerException n){
                    out.println("Missing node in " + Arrays.toString(line));
                }
            }
            br.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    
    public static void parseTitleFile(InputStream s){
        BufferedReader br;
        String[] line;
        String title;
        int id;
        
        try{
            br = new BufferedReader(new InputStreamReader(s));
            while(br.ready()){
                line = br.readLine().split(",");
                try{
                    title = line[0];
                    id = Integer.parseInt(line[1]);
                    Node.get(id).addLabel(title);
                } catch(NumberFormatException e){
                    out.println("Line fail: " + Arrays.toString(line));
                } catch(NullPointerException n){
                    out.println("Missing node in " + Arrays.toString(line));
                } catch(Exception e){
                    out.println("Line fail: " + Arrays.toString(line));
                    e.printStackTrace();
                }
            }
            br.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static void parseFile(InputStream s, FileParser parser){
        BufferedReader br;
        String[] line = new String[0];
        
        try{
            br = new BufferedReader(new InputStreamReader(s));
            while(br.ready()){
                line = br.readLine().split(",");
                parser.parse(line);
            }
        } catch(Exception e){
            out.println("Line fail: " + Arrays.toString(line));
            e.printStackTrace();
        }
    }
}

interface FileParser{
    public void parse(String[] line);
}
