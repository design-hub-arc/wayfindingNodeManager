package nodemanager.gui;

import java.awt.BorderLayout;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class ApplicationBody extends JPanel{
    private final HashMap<String, ApplicationPage> pages;
    
    private ApplicationPage currentPage;
    
    public static final String EDIT = "EDIT";
    
    public ApplicationBody(){
        pages = new HashMap<>();
        pages.put(EDIT, new EditCanvas(this));
        currentPage = null;
        
        setLayout(new BorderLayout());
        
        switchToPage(EDIT);
    }
    
    public final void switchToPage(String pageName){
        if(pages.containsKey(pageName)){
            this.removeAll();
            this.add(pages.get(pageName));
        } else {
            throw new IllegalArgumentException();
        }
    }
}
