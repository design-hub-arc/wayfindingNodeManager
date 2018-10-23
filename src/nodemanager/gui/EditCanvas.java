package nodemanager.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import nodemanager.node.*;

public class EditCanvas extends JPanel{
    private final MenuBar menu;
    private final Pane body;
    private final Sidebar sideBar;
    private final JButton chooseCsvButton;
    private final JButton chooseMapButton;
    private final NodeDataPane selectedNode;
    private MapImage map;
    
    public EditCanvas(){
        super();
        GridBagLayout lo = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(lo);
        
        menu = new MenuBar();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        add(menu, c);
        
        body = new Pane();
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 9;
        c.weighty = 9;
        c.fill = GridBagConstraints.BOTH;
        add(body, c);
        
        sideBar = new Sidebar();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 1;
        c.weighty = 9;
        c.fill = GridBagConstraints.BOTH;
        add(sideBar, c);
        
        selectedNode = new NodeDataPane();
        sideBar.add(selectedNode);
        
        map = new MapImage();
        body.add(map);
        
        chooseCsvButton = createSelector(
                "node file", 
                new String[]{"Comma Separated Values", "csv"},
                new FileSelectedListener(){
                    @Override
                    public void run(File f){
                        loadNodesFromFile(f);
                    }
                }
        );
        menu.add(chooseCsvButton);
        
        chooseMapButton = createSelector(
                "map Image", 
                new String[]{"Image file", "JPEG file", "jpg", "jpeg", "png"},
                new FileSelectedListener(){
                    @Override
                    public void run(File f){
                        map.setImage(f);
                        repaint();
                    }
                }
        );
        menu.add(chooseMapButton);
        
        map.setImage(new File(new File("").getAbsolutePath() + "/data/map.png"));
        loadNodesFromFile(new File(new File("").getAbsolutePath() + "/data/nodeData.csv"));
        
        setBackground(Color.blue);
    }
    private JButton createSelector(String type, String[] types, FileSelectedListener l){
        JButton ret = new JButton("Select " + type);
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileNameExtensionFilter(type, types));
        
        ret.addActionListener(new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                int response = chooser.showOpenDialog(chooser);
                if(response == JFileChooser.APPROVE_OPTION){
                    l.run(chooser.getSelectedFile());
                }
            }
        });
        
        return ret;
    }
    
    private void loadNodesFromFile(File f){
        NodeParser.readCsv(f);
        map.scaleTo(Node.get(-1).rawX, Node.get(-1).rawY, Node.get(-2).rawX, Node.get(-2).rawY);
        
        for(Node n : Node.getAll()){
            map.addNode(n);
            map.getIconFor(n).addMouseListener(new MouseListener(){
                @Override
                public void mouseClicked(MouseEvent me) {
                    selectedNode.selectNode(n);
                }

                @Override
                public void mousePressed(MouseEvent me) {}

                @Override
                public void mouseReleased(MouseEvent me) {}

                @Override
                public void mouseEntered(MouseEvent me) {}

                @Override
                public void mouseExited(MouseEvent me) {}
            });
        }
        revalidate();
        repaint();
    }
}