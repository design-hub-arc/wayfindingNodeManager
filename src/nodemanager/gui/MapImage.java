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
            Session.newMapX = Node.get(-1).getIcon().getX();
            Session.newMapY = Node.get(-1).getIcon().getY();
            JOptionPane.showMessageDialog(null, "Click on a point on the map to set new lower-right corner");
        } else if(Session.mode == Mode.RESCALE_LR){
            Session.mode = Mode.NONE;
            Session.newMapWidth = Node.get(-2).getIcon().getX() - Session.newMapX;
            Session.newMapHeight = Node.get(-2).getIcon().getY() - Session.newMapY;
            //setImage(buff.getSubimage(Session.newMapX, Session.newMapY, Session.newMapWidth, Session.newMapHeight));
            // create a new map image file buff.getSubimage(Session.newMapX, Session.newMapY, Session.newMapWidth, Session.newMapHeight);
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
            double shiftX = me.getX();
            double shiftY = me.getY();
            double baseX;
            double baseY;
            for(NodeIcon ni : nodeIcons.values()){
                baseX = scaler.x(ni.node.rawX);
                baseY = scaler.y(ni.node.rawY);
                ni.setLocation((int)(baseX + shiftX - ni.getWidth() - 5), (int)(baseY + shiftY - ni.getHeight() - 5));
            }
            revalidate();
            repaint();
        } else if(Session.mode == Mode.RESCALE_LR){
            scaler.setSize(me.getX() - Session.newMapX, me.getY() - Session.newMapY);
            for(NodeIcon ni : nodeIcons.values()){
                ni.initPos();
                ni.setLocation(ni.getX() + Session.newMapX + 5, ni.getY() + Session.newMapY + 5);
            }
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
