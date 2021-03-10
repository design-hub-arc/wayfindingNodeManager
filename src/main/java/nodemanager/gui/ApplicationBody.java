package nodemanager.gui;

import java.awt.BorderLayout;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import nodemanager.gui.exportData.ExportBody;
import nodemanager.gui.importData.DriveImportPage;
import nodemanager.gui.importData.LocalImportPage;

/**
 *
 * @author Matt
 */
public class ApplicationBody extends JPanel{
    private final HashMap<String, ApplicationPage> pages;
    
    private final JPanel contentArea;
    
    private ApplicationPage currentPage;
    
    public static final String EDIT = "EDIT";
    public static final String LOCAL_IMPORT = "LOCAL_IMPORT";
    public static final String REMOTE_IMPORT = "REMOTE_IMPORT";
    public static final String REMOTE_EXPORT = "REMOTE_EXPORT";
    
    public ApplicationBody(){
        pages = new HashMap<>();
        pages.put(EDIT, new EditCanvas(this));
        pages.put(LOCAL_IMPORT, new LocalImportPage(this));
        //pages.put(REMOTE_IMPORT, new DriveImportPage(this));
        //pages.put(REMOTE_EXPORT, new ExportBody(this));
        currentPage = null;
        
        setLayout(new BorderLayout());
        
        contentArea = new JPanel();
        contentArea.setLayout((new BorderLayout()));
        
        JSplitPane topAndBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        topAndBottom.setTopComponent(contentArea);
        topAndBottom.setBottomComponent(InputConsole.getInstance());
        topAndBottom.setContinuousLayout(true);
        add(topAndBottom, BorderLayout.CENTER);
        
        switchToPage(EDIT);
    }
    
    public final void switchToPage(String pageName){
        if(pages.containsKey(pageName)){
            contentArea.removeAll();
            currentPage = pages.get(pageName);
            contentArea.add(currentPage);
            revalidate();
            repaint();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
