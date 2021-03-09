package nodemanager.gui;

import java.awt.BorderLayout;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 *
 * @author Matt
 */
public class ApplicationBody extends JPanel{
    private final HashMap<String, ApplicationPage> pages;
    
    private final JPanel contentArea;
    
    private ApplicationPage currentPage;
    
    public static final String EDIT = "EDIT";
    
    public ApplicationBody(){
        pages = new HashMap<>();
        pages.put(EDIT, new EditCanvas(this));
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
            contentArea.add(pages.get(pageName));
        } else {
            throw new IllegalArgumentException();
        }
    }
}
