package nodemanager.files;

import nodemanager.io.StreamReaderUtil;
import static nodemanager.io.StreamReaderUtil.NEWLINE;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import nodemanager.io.DriveIOOp;
import nodemanager.io.GoogleDriveUploader;
import static nodemanager.io.GoogleDriveUploader.DOWNLOAD_URL_PREFIX;

/**
 * The Manifest file is used by Wayfinding to specify which
 * other files are meant to be used together.
 * It provides URLs to a node coordinate file,
 * node connection file, node label file, and a map image,
 * so Wayfinding has an easier time finding them.
 * 
 * @author Matt Crow
 */
public class WayfindingManifest extends AbstractCsvFile{
    private final String title;
    private String driveFolderId;
    private final HashMap<FileType, AbstractWayfindingFile> attachedFiles;
    private final HashMap<FileType, String> urls;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    
    public WayfindingManifest(String folderName){
        super(folderName + "Manifest", FileType.MANIFEST);
        title = folderName;
        driveFolderId = null;
        attachedFiles = new HashMap<>();
        urls = new HashMap<>();
    }
    
    public WayfindingManifest(){
        this(LocalDateTime.now().format(DATE_FORMAT));
    }
    
    public String getTitle(){
        return title;
    }
    
    public void setDriveFolderId(String folderId){
        driveFolderId = folderId;
    }
    
    public String getDriveFolderId(){
        return driveFolderId;
    }
    
    public final boolean containsUrlFor(FileType fileType) {
        return urls.containsKey(fileType);
    }
    
    public final DriveIOOp<AbstractWayfindingFile> getFileFor(FileType fileType){
        DriveIOOp<AbstractWayfindingFile> ret;
        
        if(attachedFiles.containsKey(fileType)){
            ret = new DriveIOOp<AbstractWayfindingFile>(){
                @Override
                public AbstractWayfindingFile perform() throws Exception {
                    return attachedFiles.get(fileType);
                }
            };
        } else {
            //load the file
            if(!containsUrlFor(fileType)){
                //cannot load file, as I don't have a URL for it
                throw new NullPointerException(String.format("No file set for type '%s'", fileType.getTitle()));
            }
            //download the file
            ret = new DriveIOOp<AbstractWayfindingFile>() {
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
                        attachedFiles.put(fileType, file); //cache the file
                    }).getExcecutingThread().join();

                    return file;
                }
            };
        }
        
        return ret;
    }
    
    public final String getUrlFor(FileType type){
        return urls.get(type);
    }
    
    @Override
    public void setContents(InputStream s) throws IOException {
        attachedFiles.clear();
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

    
    @Override
    public void exportData() {
        attachedFiles.clear();
        urls.clear();
        
        NodeCoordFile coords = new NodeCoordFile(title);
        coords.exportData();
        attachedFiles.put(FileType.NODE_COORD, coords);
        
        NodeConnFile conn = new NodeConnFile(title);
        conn.exportData();
        attachedFiles.put(FileType.NODE_CONN, conn);
        
        NodeLabelFile labels = new NodeLabelFile(title);
        labels.exportData();
        attachedFiles.put(FileType.LABEL, labels);
        
        MapFile map = new MapFile(title);
        map.exportData();
        attachedFiles.put(FileType.MAP_IMAGE, map);
    }
    
    /**
     * Uploads the contents of the program to the drive,
     * then populates this with the urls of those new files.
     * @return 
     */
    public DriveIOOp<Boolean> uploadContents(){
        if(attachedFiles.isEmpty()){
            exportData();
        }
        return new DriveIOOp<Boolean>(){
            @Override
            public Boolean perform() throws Exception {
                attachedFiles.forEach((type, file)->{
                    try {
                        GoogleDriveUploader.uploadFile(file, driveFolderId).addOnSucceed((f)->{
                            urls.put(type, DOWNLOAD_URL_PREFIX + f.getId());
                        }).getExcecutingThread().join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });
                
                return true;
            }
        };
    }
}
