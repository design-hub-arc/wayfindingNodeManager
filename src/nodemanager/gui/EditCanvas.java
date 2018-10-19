package nodemanager.gui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import nodemanager.node.*;

public class EditCanvas extends JPanel{
    private final MenuBar menu;
    private final Pane body;
    private final JButton chooseCsvButton;
    private final JButton chooseMapButton;
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
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1;
        c.weighty = 9;
        c.fill = GridBagConstraints.BOTH;
        add(body, c);
        
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
        
        System.out.println(body.getY());
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
        
        Scale s = map.getScale();
        NodeIcon ni;
        for(Node n : Node.getAll()){
            ni = new NodeIcon(n);
            ni.scaleTo(s);
            map.add(ni);
        }
        revalidate();
        repaint();
    }
}