package files;

import com.google.api.services.drive.model.File;
import io.StreamReaderUtil;
import static io.StreamReaderUtil.NEWLINE;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static java.lang.System.out;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import nodemanager.io.DriveIOOp;
import nodemanager.io.GoogleDriveUploader;

/**
 * creates a manifest file containing all the currently active data,
 * then upload each file to the google drive,
 * export them to the web,
 * and paste their URLs into the manifest
 * @author Matt Crow
 */
public class WayfindingManifest extends AbstractCsvFile{
    private final String title;
    private String driveFolder;
    private final HashMap<FileType, String> urls;
    private static final String DOWNLOAD_URL_PREFIX = "https://drive.google.com/uc?export=download&id=";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    
    public WayfindingManifest(String folderName){
        super(folderName + "Manifest", FileType.MANIFEST);
        title = folderName;
        driveFolder = null;
        urls = new HashMap<>();
    }
    
    public WayfindingManifest(){
        this(LocalDateTime.now().format(DATE_FORMAT));
    }
    
    /**
     * Asynchronously downloads a manifest from the drive
     * 
     * @param id the file id or url of the manifest
     * @return the DriveIIOp downloading the manifest
     */
    public static DriveIOOp<WayfindingManifest> downloadManifest(String id){
        if(id.contains("id=")){
            return downloadManifest(id.split("id=")[1]);
        }
        DriveIOOp<WayfindingManifest> ret = new DriveIOOp<WayfindingManifest>(){
            @Override
            public WayfindingManifest perform() throws Exception {
                WayfindingManifest m = new WayfindingManifest("");
                
                //download the file from the drive
                GoogleDriveUploader.download(id).addOnSucceed((stream)->{
                    try {
                        m.setContents(stream); //populate
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).getExcecutingThread().join(); //wait until it's done
                return m;
            }
        };
        return ret;
    }
    
    public final boolean containsUrlFor(FileType fileType) {
        return urls.containsKey(fileType);
    }
    
    public final DriveIOOp<AbstractWayfindingFile> getFileFor(FileType fileType){
        if(!containsUrlFor(fileType)){
            throw new NullPointerException(String.format("No file set for type '%s'", fileType.getTitle()));
        } 
        
        DriveIOOp<AbstractWayfindingFile> ret = new DriveIOOp<AbstractWayfindingFile>() {
            @Override
            public AbstractWayfindingFile perform() throws Exception {
                AbstractWayfindingFile file = AbstractWayfindingFile.fromType("manifestFile", fileType);
                String id = urls.get(fileType).replace(DOWNLOAD_URL_PREFIX, "");
                
                GoogleDriveUploader.download(id).addOnSucceed((in)->{
                    try {
                        file.setContents(in);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).getExcecutingThread().join();
                
                return file;
            }
        };
        
        return ret;
    }
    
    public final String getUrlFor(FileType type){
        return urls.get(type);
    }
    
    public DriveIOOp<File> upload(String folderId){
        WayfindingManifest m = this;
        return new DriveIOOp<File>(){
            @Override
            public File perform() throws Exception {                
                GoogleDriveUploader
                        .createSubfolder(folderId, title)
                        .addOnSucceed((folder)->{
                            m.driveFolder = folder.getId();
                        }).getExcecutingThread().join();
                GoogleDriveUploader
                    .uploadFile(m, m.driveFolder)
                    .getExcecutingThread()
                    .join();
                return null;
            }
        };
    }
    
    @Override
    public void setContents(InputStream s) throws IOException {
        urls.clear();
        
        String contents = StreamReaderUtil.readStream(s);
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
    }
    
    @Override
    public String getContentsToWrite() {
        StringBuilder sb = new StringBuilder("Data, URL");
        urls.entrySet().forEach((entry) -> {
            sb
                .append(NEWLINE)
                .append(entry.getKey().getTitle())
                .append(", ")
                .append(entry.getValue());
        });
        return sb.toString();
    }

    @Override
    public void importData() {
        urls.forEach((type, url)->{
            try {
                getFileFor(type).addOnSucceed((file)->{
                    file.importData();
                }).getExcecutingThread().join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Uploads the contents of the program to the drive,
     * then populates this with the urls of those new files.
     */
    @Override
    public void exportData() {
        urls.clear();
        
        NodeCoordFile coords = new NodeCoordFile(title);
        coords.exportData();
        
        NodeConnFile conn = new NodeConnFile(title);
        conn.exportData();
        
        NodeLabelFile labels = new NodeLabelFile(title);
        labels.exportData();
        
        MapFile map = new MapFile(title);
        map.exportData();
        
        DriveIOOp populate = new DriveIOOp<Boolean>(){
            @Override
            public Boolean perform() throws Exception {
                GoogleDriveUploader.uploadFile(coords, driveFolder).addOnSucceed((f)->{
                    urls.put(FileType.NODE_COORD, DOWNLOAD_URL_PREFIX + ((com.google.api.services.drive.model.File)f).getId());
                }).getExcecutingThread().join();
                
                GoogleDriveUploader.uploadFile(conn, driveFolder).addOnSucceed((f)->{
                    urls.put(FileType.NODE_CONN, DOWNLOAD_URL_PREFIX + ((com.google.api.services.drive.model.File)f).getId());
                }).getExcecutingThread().join();
                
                GoogleDriveUploader.uploadFile(labels, driveFolder).addOnSucceed((f)->{
                    urls.put(FileType.LABEL, DOWNLOAD_URL_PREFIX + ((com.google.api.services.drive.model.File)f).getId());
                }).getExcecutingThread().join();
                
                GoogleDriveUploader.uploadFile(map, driveFolder).addOnSucceed((f)->{
                    urls.put(FileType.MAP_IMAGE, DOWNLOAD_URL_PREFIX + ((com.google.api.services.drive.model.File)f).getId());
                }).getExcecutingThread().join();
                
                return true;
            }
        };
        
        try {
            populate.getExcecutingThread().join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
