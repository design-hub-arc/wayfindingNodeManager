package nodemanager.files;

import com.google.api.services.drive.model.File;
import nodemanager.io.StreamReaderUtil;
import static nodemanager.io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import nodemanager.exceptions.NoPermissionException;
import nodemanager.io.GoogleDriveUploader;
import static nodemanager.io.GoogleDriveUploader.DOWNLOAD_URL_PREFIX;
import nodemanager.model.Graph;

/**
 * The Manifest file is used by Wayfinding to specify which
 * other files are meant to be used together.
 * It provides URLs to a node coordinate file,
 * node connection file, node label file, and a map image,
 * so Wayfinding has an easier time finding them.
 * 
 * @author Matt Crow
 */
public class WayfindingManifest extends AbstractWayfindingFileHelper {
    private final String title;
    private final HashMap<FileType, AbstractWayfindingFileHelper> attachedFiles;
    private final HashMap<FileType, String> urls;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    
    public WayfindingManifest(String folderName){
        super(folderName + "Manifest", FileType.MANIFEST);
        title = folderName;
        attachedFiles = new HashMap<>();
        urls = new HashMap<>();
    }
    
    public WayfindingManifest(){
        this(LocalDateTime.now().format(DATE_FORMAT));
    }
    
    public String getTitle(){
        return title;
    }
    
    public final boolean containsUrlFor(FileType fileType) {
        return urls.containsKey(fileType);
    }
    
    public final AbstractWayfindingFileHelper importFileFor(FileType fileType, Graph g){
        AbstractWayfindingFileHelper ret;
        
        if(attachedFiles.containsKey(fileType)){
            ret = attachedFiles.get(fileType);
        } else {
            //load the file
            if(!containsUrlFor(fileType)){
                //cannot load file, as I don't have a URL for it
                throw new NullPointerException(String.format("No file set for type '%s'", fileType.getTitle()));
            }
            //download the file
            AbstractWayfindingFileHelper file = AbstractWayfindingFileHelper.fromType("manifestFile", fileType);
            String id = urls.get(fileType).replace(DOWNLOAD_URL_PREFIX, "");

            try {
                InputStream in = GoogleDriveUploader.download(id);
                file.readGraphDataFromFile(g, in);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            attachedFiles.put(fileType, file); //cache the file

            ret = file;
        }
        
        return ret;
    }

    @Override
    public void readGraphDataFromFile(Graph g, InputStream in) throws IOException {
        // gather URLs
        attachedFiles.clear();
        urls.clear();
        
        String contents = StreamReaderUtil.readStream(in);
        String[] lines = contents.split("\\n");
        
        String[] line;
        FileType type;
        String url;
        for(int i = 1; i < lines.length; i++){
            line = lines[i].split(",");
            type = FileType.fromTitle(line[0].trim());
            url = line[1].trim();
            urls.put(type, url);
        }
        
        // download the files
        urls.forEach((fileType, fileUrl)->{
            importFileFor(fileType, g);
        });
    }

    @Override
    public void writeGraphDataToFile(Graph g, OutputStream out) throws IOException {
        StringBuilder sb = new StringBuilder("Data, URL");
        urls.entrySet().forEach((entry) -> {
            sb
                .append(NEWLINE)
                .append(entry.getKey().getTitle())
                .append(", ")
                .append(entry.getValue());
        });
        out.write(sb.toString().getBytes());
    }
    
    public final com.google.api.services.drive.model.File uploadToDrive(String folderId, Graph g) throws IOException, NoPermissionException{
        // create a subfolder for this
        File newFolder = GoogleDriveUploader.createSubfolder(folderId, title);
        
        // try and upload the different files for the Graph
        attachedFiles.clear();
        urls.clear();
        
        NodeCoordFileHelper coords = new NodeCoordFileHelper(title);
        attachedFiles.put(FileType.NODE_COORD, coords);
        
        NodeConnFileHelper conn = new NodeConnFileHelper(title);
        attachedFiles.put(FileType.NODE_CONN, conn);
        
        NodeLabelFileHelper labels = new NodeLabelFileHelper(title);
        attachedFiles.put(FileType.LABEL, labels);
        
        MapFileHelper map = new MapFileHelper(title);
        attachedFiles.put(FileType.MAP_IMAGE, map);
        
        attachedFiles.forEach((type, file)->{
            try {
                File f = GoogleDriveUploader.uploadFile(g, file, newFolder.getId());
                urls.put(type, DOWNLOAD_URL_PREFIX + f.getId());
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (NoPermissionException ex) {
                ex.printStackTrace();
            } 
        });
        
        // lastly, upload this to the Drive.
        return GoogleDriveUploader.uploadFile(g, this, newFolder.getId());
    }
}
