package nodemanager.node;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import static java.lang.System.out;
import java.util.Arrays;

public class NodeParser {
    public static void parseNodeFile(File f){
        BufferedReader br;
        String[] line;
        int id;
        double x;
        double y;
        
        try{
            br = new BufferedReader(new FileReader(f));
            while(br.ready()){
                line = br.readLine().split(",");
                try{
                    id = Integer.parseInt(line[0].trim());
                    x = Double.parseDouble(line[1].trim());
                    y = Double.parseDouble(line[2].trim());
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
    public static void parseConnFile(File f){
        BufferedReader br;
        String[] line;
        int id;
        int adj;
        
        try{
            br = new BufferedReader(new FileReader(f));
            while(br.ready()){
                line = br.readLine().split(",");
                try{
                    id = Integer.parseInt(line[0].trim());
                    adj = Integer.parseInt(line[1].trim());
                    Node.get(id).addAdjId(adj);
                    Node.get(adj).addAdjId(id);
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
}
