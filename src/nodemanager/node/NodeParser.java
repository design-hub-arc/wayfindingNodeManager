package nodemanager.node;

import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;
import static java.lang.System.out;

public class NodeParser {
    private static enum ColumnType{
        ID,
        X,
        Y,
        CONNECTION
    }
    private static HashMap<ColumnType, Integer> columns = new HashMap<>();
    
    public static void readCsv(File f){
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(f));
            br.lines().forEach(l -> parseLine(l));
            Node.init();
            Node.logAll();
        } catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
    
    public static void parseLine(String s){
        String[] split = s.split(",");
        try{
            int id = Integer.parseInt(split[columns.get(ColumnType.ID)]);
            double x = Double.parseDouble(split[columns.get(ColumnType.X)]);
            double y = Double.parseDouble(split[columns.get(ColumnType.Y)]);
            new Node(id, x, y);
            
            try{
                int adj = Integer.parseInt(split[columns.get(ColumnType.CONNECTION)]);
                Node.get(id).addAdjId(adj);
            } catch(Exception e){
                //doesn't have adj
                //e.printStackTrace();
            }
        } catch(Exception e){
            setColumnsByHeaders(split);
            out.println("Line fail: " + s);
            //e.printStackTrace();
        }
        
    }
    
    //TODO: find some way to make this shorter
    private static void setColumnsByHeaders(String[] headers){
        for(int i = 0; i < headers.length; i++){
            String header = headers[i].toUpperCase();
            if(header.equals("NODE") || header.equals("ID") || header.equals("NODE ID")){
                columns.put(ColumnType.ID, i);
            } else if(header.equals("X") || header.equals("X COORDINATE") || header.equals("POSITION X")){
                columns.put(ColumnType.X, i);
            } else if(header.equals("Y") || header.equals("Y COORDINATE") || header.equals("POSITION Y")){
                columns.put(ColumnType.Y, i);
            } else if(header.equals("CONNECTED NODE") || header.equals("CONNECT-TO") || header.equals("ADJACENT")){
                columns.put(ColumnType.CONNECTION, i);
            }
        }
        out.println(columns.toString());
    }
}
