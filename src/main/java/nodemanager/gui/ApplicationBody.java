package nodemanager.gui;

import nodemanager.gui.editPage.EditCanvas;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.function.Supplier;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import nodemanager.Mode;
import nodemanager.Session;
import nodemanager.gui.exportData.ExportBody;
import nodemanager.gui.importData.DriveImportPage;
import nodemanager.gui.importData.LocalImportPage;

/**
 *
 * @author Matt
 */
public class ApplicationBody extends JPanel{
    private final HashMap<String, Supplier<ApplicationPage>> pages;
    
    private final JPanel contentArea;
    
    private ApplicationPage currentPage;
    
    public static final String EDIT = "EDIT";
    public static final String LOCAL_IMPORT = "LOCAL_IMPORT";
    public static final String REMOTE_IMPORT = "REMOTE_IMPORT";
    public static final String REMOTE_EXPORT = "REMOTE_EXPORT";
    
    public ApplicationBody(){
        pages = new HashMap<>();
        pages.put(EDIT, ()-> new EditCanvas(this));
        pages.put(LOCAL_IMPORT, ()-> new LocalImportPage(this));
        pages.put(REMOTE_IMPORT, ()-> new DriveImportPage(this));
        pages.put(REMOTE_EXPORT, ()-> new ExportBody(this));
        currentPage = null;
        
        setLayout(new BorderLayout());
        
        contentArea = new JPanel();
        contentArea.setLayout((new BorderLayout()));
        
        JSplitPane topAndBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        topAndBottom.setResizeWeight(0.75);
        topAndBottom.setTopComponent(contentArea);
        topAndBottom.setBottomComponent(InputConsole.getInstance());
        topAndBottom.setContinuousLayout(true);
        add(topAndBottom, BorderLayout.CENTER);
        
        this.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "exit cmd");
        this.getActionMap().put("exit cmd", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Session.setMode(Mode.NONE);
            }
        });
        
        switchToPage(EDIT);
    }
    
    public final void switchToPage(String pageName){
        if(pageName.equalsIgnoreCase(EDIT)){
            editPage();
        }else if(pages.containsKey(pageName)){
            setPage(pages.get(pageName).get());
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    private void setPage(ApplicationPage page){
        contentArea.removeAll();
        currentPage = page;
        contentArea.add(currentPage);
        revalidate();
        repaint();
    }
    
    public final void editPage(){
        EditCanvas editPage = new EditCanvas(this);
        Session.map = editPage.getMapImage();
        if(Session.getCurrentDataSet() != null){
            editPage.renderGraph(Session.getCurrentDataSet());
        }
        setPage(editPage);
    }
}
