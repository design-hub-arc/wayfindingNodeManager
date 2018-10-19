package nodemanager.gui;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import java.awt.Color;
import java.util.Collection;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import nodemanager.node.Node;


import nodemanager.node.NodeParser;

public class EditCanvas extends JPanel{
    private JButton chooseCsvButton;
    private JFileChooser csvFileChooser;
    
    private JButton chooseMapButton;
    private JFileChooser mapFileChooser;
    private MapImage map;
    
    public EditCanvas(){
        super();
        map = new MapImage();
        add(map);
        
        setBackground(Color.blue);
        
        csvFileChooser = new JFileChooser();
        csvFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        csvFileChooser.setFileFilter(new FileNameExtensionFilter("Comma Separated Values", "csv"));
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image file", "JPEG file", "jpg", "jpeg", "png");
        mapFileChooser = new JFileChooser();
        mapFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        mapFileChooser.setFileFilter(filter);
        
        chooseCsvButton = new JButton("Choose a node file");
        chooseCsvButton.addActionListener(new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                int ret = csvFileChooser.showOpenDialog(chooseMapButton);
                if(ret == JFileChooser.APPROVE_OPTION){
                    NodeParser.readCsv(csvFileChooser.getSelectedFile());
                }
            }
        });
        add(chooseCsvButton);
        
        chooseMapButton = new JButton("Choose a Map Image");
        chooseMapButton.addActionListener(new AbstractAction(){
            public void actionPerformed(ActionEvent e){
                int ret = mapFileChooser.showOpenDialog(chooseMapButton);
                if(ret == JFileChooser.APPROVE_OPTION){
                    File file = mapFileChooser.getSelectedFile();
                    map.setImage(file);
                    repaint();
                }
            }
        });
        add(chooseMapButton);
        
        NodeParser.readCsv(new File(new File("").getAbsolutePath() + "/data/nodeData.csv"));
        map.setImage(new File(new File("").getAbsolutePath() + "/data/map.png"));
        map.scaleTo(Node.get(-1).rawX, Node.get(-1).rawY, Node.get(-2).rawX, Node.get(-2).rawY);
        
        Scale s = map.getScale();
        Collection<Node> all = Node.getAll();
        NodeIcon ni;
        for(Node n : all){
            ni = new NodeIcon(n);
            ni.scaleTo(s);
            map.add(ni);
        }
        map.displayData();
        revalidate();
        repaint();
    }
    /*
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //map.paintComponent(g);
        
        System.out.println(this.getComponentCount());
        //flickering
    }*/
}
