package nodemanager.save;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Work in progress. Will create a manifest file containing all the currently active data,
 * then upload each file to the google drive,
 * export them to the web,
 * and paste their URLs into the manifest
 * @author Matt Crow
 */
public class WayfindingManifest {
    private final String title;
    private final HashMap<String, String> urls;
    
    public WayfindingManifest(){
        title = "manifest" + new SimpleDateFormat("MM_dd_yyyy").format(Calendar.getInstance().getTime()) + ".csv";
        urls = new HashMap<>();
    }
    
    public File export(String path){
        File f = null;
        BufferedWriter out = null;
        String nl = System.getProperty("line.separator");
        
        String time = new SimpleDateFormat("MM_dd_yyyy").format(Calendar.getInstance().getTime());
        
        try {
            f = new File(path + File.separator + "wayfindingManifest" + time + ".csv");
            
            out = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
            out.write("Data, URL" + nl);
            
            for(Map.Entry<String, String> entry : urls.entrySet()){
                out.write(entry.getKey() + ", " + entry.getValue() + nl);
            }
            
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return f;
    }
}
