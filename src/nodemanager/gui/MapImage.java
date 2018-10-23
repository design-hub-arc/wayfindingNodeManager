package nodemanager.gui;

import java.io.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.util.HashMap;
import nodemanager.node.Node;

/*
MapImage is used to render an image,
as well as proved a scale to resize points drawn onto it.

Upon creating a MapImage, you need to call setImage on a file,
then scaleTo a set of coordinates
*/

public class MapImage extends JLabel implements MouseListener{
    private BufferedImage buff;
    private final Scale scaler;
    private final HashMap<Integer, NodeIcon> nodeIcons;
    
    public MapImage(){
        super();
        setVisible(true);
        scaler = new Scale();
        nodeIcons = new HashMap<>();
        addMouseListener(this);
    }
    
    public void addNode(Node n){
        NodeIcon ni = new NodeIcon(n);
        ni.scaleTo(scaler);
        nodeIcons.put(n.id, ni);
        add(ni);
        revalidate();
        repaint();
    }
    public NodeIcon getIconFor(Node n){
        return nodeIcons.get(n.id);
    }
    
    public void setImage(File f){
        try{
            buff = ImageIO.read(f);
            setIcon(new ImageIcon(buff));
            setSize(buff.getWidth(), buff.getHeight());
            scaler.setSource(this); //need to reinvoke b/c size passed by ref
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void scaleTo(double x1, double y1, double x2, double y2){
        scaler.rescale(x1, y1, x2, y2);
    }
    
    public Scale getScale(){
        return scaler;
    }
    
    

    @Override
    public void mouseClicked(MouseEvent me) {
        System.out.println(scaler.inverseX(me.getX()) + ", " + scaler.inverseY(me.getY()));
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
}
