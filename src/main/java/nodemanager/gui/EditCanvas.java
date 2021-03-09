package nodemanager.gui;

import nodemanager.gui.mapComponents.MapImage;
import nodemanager.*;
import javax.swing.*;
import java.awt.*;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */
public class EditCanvas extends ApplicationPage {

    /**
     * EditCanvas is the main JPanel used by the program
     */
    
    private final JComponent body;
    private final Sidebar sideBar;

    private final NodeDataPane selectedNode;
    private MapImage map;

    /**
     * Creates many different components, then adds them to the JPanel.
     */
    public EditCanvas(ApplicationBody parent) {
        super(parent);

        Session.currentPanel = this;
        
        setLayout(new BorderLayout());

        JSplitPane content = new JSplitPane();
        content.setContinuousLayout(true);
        add(content, BorderLayout.CENTER);
        
        body = new JPanel();
        body.setLayout(new FlowLayout());
        content.setRightComponent(body);
        
        sideBar = new Sidebar();
        content.setLeftComponent(sideBar);

        selectedNode = new NodeDataPane();
        sideBar.add(selectedNode);
        Session.dataPane = selectedNode;
        
        body.setLayout(new GridLayout(1, 1));
        map = new MapImage();
        body.add(map);
        
        setBackground(Color.blue);
        revalidate();
        repaint();
    }
    
    
}
