package nodemanager.gui;

import java.awt.Component;
import java.awt.Graphics2D;
import java.io.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import nodemanager.node.Node;
import nodemanager.*;

/*
MapImage is used to render an image,
as well as proved a scale to resize points drawn onto it.

Upon creating a MapImage, you need to call setImage on a file,
then scaleTo a set of coordinates
*/

public class MapImage extends JLabel implements MouseListener, MouseMotionListener, MouseWheelListener{
    private BufferedImage buff;
    private final Scale scaler;
    private final HashMap<Integer, NodeIcon> nodeIcons;
    
    private double zoom;
    
    //https://stackoverflow.com/questions/874360/swing-creating-a-draggable-component
    private volatile int screenX;
    private volatile int screenY;
    private volatile int prevX;
    private volatile int prevY;
    
    public MapImage(){
        super();
        setVisible(true);
        scaler = new Scale();
        nodeIcons = new HashMap<>();
        
        zoom = 1.0;
        
        screenX = 0;
        screenY = 0;
        prevX = getX();
        prevY = getY();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    public void addNode(Node n){
        NodeIcon ni = n.getIcon();
        ni.scaleTo(scaler);
        nodeIcons.put(n.id, ni);
        
        add(ni);
        revalidate();
        repaint();
    }
    public void removeAllNodes(){
        ArrayList<Component> newComp = new ArrayList<>();
        for(Component c : getComponents()){
            if(!(c instanceof NodeIcon)){
                newComp.add(c);
            }
        }
        removeAll();
        for(Component c : newComp){
            add(c);
        }
    }
    private void resizeNodeIcons(){
        nodeIcons.values().forEach(n -> n.scaleTo(scaler));
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
        if(Session.mode == Mode.ADD){
            Node n = new Node(scaler.inverseX(me.getX()), scaler.inverseY(me.getY()));
            n.init();
            addNode(n);
            Session.mode = Mode.NONE;
        } else if(Session.mode == Mode.MOVE){
            Session.mode = Mode.NONE;
        } else if(Session.mode == Mode.RESCALE_UL){
            Session.mode = Mode.RESCALE_LR;
            Node.get(-1).getIcon().setLocation((int)scaler.inverseX(me.getX()), (int)scaler.inverseY(me.getY()));
            JOptionPane.showMessageDialog(null, "Click on a point on the map to set new lower-right corner");
        } else if(Session.mode == Mode.RESCALE_LR){
            Session.mode = Mode.NONE;
            Node.get(-2).getIcon().setLocation((int)scaler.inverseX(me.getX()), (int)scaler.inverseY(me.getY()));
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        screenX = me.getX();
        screenY = me.getY();
        prevX = getX();
        prevY = getY();
    }

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {
        if(Session.mode == Mode.ADD){
            //TODO: add node icon that follows mouse?
        }
    }

    @Override
    public void mouseExited(MouseEvent me) {}

    @Override
    public void mouseDragged(MouseEvent me) {
        //click and drag. Flickering
        setLocation(prevX + (me.getX() - screenX), prevY + (me.getY() - screenY));
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if(Session.mode == Mode.MOVE){
            Session.selectedNode.getIcon().setLocation(me.getX(), me.getY() + 5);
            Session.selectedNode.getIcon().drawAllLinks();
            revalidate();
            repaint();
        } else if(Session.mode == Mode.RESCALE_UL){
            // wait a second... maybe I shouldn't reposition nodes, but instead resize the map image based on where the user clicks
            Node.get(-1).getIcon().setLocation(me.getX(), me.getY() + 5);
            scaler.rescale(scaler.inverseX(me.getX()), scaler.inverseY(me.getY()), Node.get(-2).rawX, Node.get(-2).rawY);
            nodeIcons.values().stream().forEach(ni -> ni.initPos());
            revalidate();
            repaint();
        } else if(Session.mode == Mode.RESCALE_LR){
            scaler.setSize(me.getX(), me.getY());
            nodeIcons.values().stream().forEach(ni -> ni.initPos());
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        zoom -= mwe.getPreciseWheelRotation() / 100;
        int newWidth = (int)(buff.getWidth() * zoom);
        int newHeight = (int)(buff.getHeight() * zoom);
        
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.OPAQUE);
        Graphics2D g = resized.createGraphics();
        g.drawImage(buff, 0, 0, newWidth, newHeight, null);
        g.dispose();
        setIcon(new ImageIcon(resized));
        scaler.setSource(this);
        resizeNodeIcons();
    }
}
