package nodemanager.gui;

import javax.swing.JPanel;

/**
 *
 * @author Matt
 */
public class ApplicationPage extends JPanel {
    private final ApplicationBody parent;
    
    public ApplicationPage(ApplicationBody parent){
        super();
        this.parent = parent;
    }
    
    public final ApplicationBody getApplicationBody(){
        return parent;
    }
}
