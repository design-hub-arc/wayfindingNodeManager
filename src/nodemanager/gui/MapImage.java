package nodemanager.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.*;
import static java.lang.System.out;
import java.util.*;
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
    
    /*
    these are the dimensions of the clip
    of the map image that the program displays
    */
    private int clipX;
    private int clipY;
    private int clipW;
    private int clipH;
    private int origClipW;
    private int origClipH;
    
    private double aspectRatio;
    
    public MapImage(){
        super();
        setVisible(true);
        scaler = new Scale();
        nodeIcons = new HashMap<>();
        
        zoom = 1.0;
        
        clipX = 0;
        clipY = 0;
        clipW = 0;
        clipH = 0;
        origClipW = 0;
        origClipH = 0;
        
        setBackground(Color.BLACK);
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addComponentListener(new ComponentListener(){
            @Override
            public void componentResized(ComponentEvent ce) {
                resize();
            }
            @Override
            public void componentMoved(ComponentEvent ce) {}
            @Override
            public void componentShown(ComponentEvent ce) {}
            @Override
            public void componentHidden(ComponentEvent ce) {}
        });
    }
    
    private int translateClickX(int x){
        return x + clipX;
    }
    private int translateClickY(int y){
        return y + clipY;
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
        nodeIcons.clear();
        ArrayList<Component> newComp = new ArrayList<>();
        for(Component c : getComponents()){
            if(!(c instanceof NodeIcon)){
                newComp.add(c);
            }
        }
        removeAll();
        newComp.stream().forEach(c -> add(c));
    }
    
    private void resizeNodeIcons(){
        nodeIcons.values().forEach(n -> n.scaleTo(scaler));
        nodeIcons.values().forEach(n -> n.setLocation((int) ((n.getX() - clipX) / zoom), (int) ((n.getY() - clipY) / zoom)));
    }
    
    private void pan(int x, int y){
        clipX += x;
        clipY += y;
        
        if(clipX < 0){
            clipX = 0;
        } else if(clipX + clipW > buff.getWidth()){
            clipX = buff.getWidth() - clipW;
        }
        
        if(clipY < 0){
            clipY = 0;
        } else if(clipY + clipH > buff.getHeight()){
            clipY = buff.getHeight() - clipH;
        }
        
        resizeNodeIcons();
    }
    private void zoom(double perc){
        zoom += perc;
        
        clipW = (int)(origClipW * zoom);
        clipH = (int)(origClipH * zoom);
        
        out.println(zoom);
        
        if(clipX + clipW >= buff.getWidth() || clipY + clipH >= buff.getHeight()){
            clipW = buff.getWidth() - clipX;
            clipH = buff.getHeight() - clipY;
        }
        if(zoom > 1.0){
            zoom = 1.0;
            clipW = origClipW;
            clipH = origClipH;
        } else if(zoom < 0.1){
            zoom = 0.1;
            clipW = origClipW / 10;
            clipH = origClipH / 10;
        }
        
        resizeNodeIcons();
    }
    
    private void resize(){
        clipW = (getWidth() < buff.getWidth()) ? getWidth() : buff.getWidth();
        clipH = (getHeight() < buff.getHeight()) ? getHeight() : buff.getHeight();
        origClipW = clipW;
        origClipH = clipH;
        zoom = 1.0;
        scaler.setSize(buff.getWidth(), buff.getHeight());
        resizeNodeIcons();
    }
    
    public void setImage(BufferedImage bi){
        buff = bi;
        aspectRatio = 1.0 * buff.getWidth() / buff.getHeight();
        clipX = 0;
        clipY = 0;
        resize();
        revalidate();
        repaint();
    }
    
    public void scaleTo(double x1, double y1, double x2, double y2){
        scaler.rescale(x1, y1, x2, y2);
    }
    
    public void saveImage(){
        JFileChooser cd = new JFileChooser();
        cd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        try {
            if(cd.showDialog(cd, "Select a location to place the new map file") == JFileChooser.APPROVE_OPTION){
                File f = new File(cd.getSelectedFile().getPath() + File.separator + "mapImage" + System.currentTimeMillis() + ".png");
                ImageIO.write(buff, "png", f);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        switch(Session.mode){
            case ADD:
                Node n = new Node(scaler.inverseX(translateClickX(me.getX())), scaler.inverseY(translateClickY(me.getY())));
                n.init();
                addNode(n);
                revalidate();
                repaint();
                Session.mode = Mode.NONE;
                break;
            case MOVE:
                Session.selectedNode.repos(scaler.inverseX(translateClickX(me.getX())), scaler.inverseY(translateClickY(me.getY())));
                Session.mode = Mode.NONE;
                break;
            case RESCALE_UL:
                Session.mode = Mode.RESCALE_LR;
                Session.newMapX = Node.get(-1).getIcon().getX();
                Session.newMapY = Node.get(-1).getIcon().getY();
                JOptionPane.showMessageDialog(null, "Position the upper left corner of node -2 at the lower right corner of where you want to crop");
                break;
            case RESCALE_LR:
                Session.mode = Mode.NONE;
                Session.newMapWidth = Node.get(-2).getIcon().getX() - Session.newMapX;
                Session.newMapHeight = Node.get(-2).getIcon().getY() - Session.newMapY;
                
                int[] clip = new int[]{Session.newMapX, Session.newMapY, Session.newMapWidth, Session.newMapHeight};
                
                out.print("Clip: ");
                for(int i : clip){
                    out.print(i + " ");
                }
                
                if(clip[0] < 0){
                    clip[0] = 0;
                }
                if(clip[1] < 0){
                    clip[1] = 0;
                }
                if(clip[2] > buff.getWidth() - clip[0]){
                    clip[2] = buff.getWidth() - clip[0];
                }
                if(clip[3] > buff.getHeight() - clip[1]){
                    clip[3] = buff.getHeight() - clip[1];
                }
                
                out.println();
                for(int i : clip){
                    out.print(i + " ");
                }
                
                setImage(buff.getSubimage(clip[0], clip[1], clip[2], clip[3]));
                break;
        }
    }
    
    @Override
    public void mousePressed(MouseEvent me) {}

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
    public void mouseDragged(MouseEvent me) {}

    @Override
    public void mouseMoved(MouseEvent me) {
        switch(Session.mode){
            case MOVE:
                Session.selectedNode.getIcon().setLocation(me.getX(), me.getY() + 5);
                Session.selectedNode.getIcon().drawAllLinks();
                revalidate();
                repaint();
                break;
            case RESCALE_UL:
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
                break;
            case RESCALE_LR:
                scaler.setSize(me.getX() - Session.newMapX, me.getY() - Session.newMapY);
                for(NodeIcon ni : nodeIcons.values()){
                    //ni.repos();
                    ni.setLocation((int)ni.node.getX() + Session.newMapX + 5, (int)ni.node.getY() + Session.newMapY + 5);
                    revalidate();
                    repaint();
                }
                break;
        }//end switch
        
        if(me.getY() < getHeight() * 0.1){
            pan(0, -5);
        } else if(me.getY() > getHeight() * 0.9){
            pan(0, 5);
        }
        
        if(me.getX() < getWidth() * aspectRatio * 0.1){
            pan(-5, 0);
        } else if(me.getX() > getWidth() * aspectRatio * 0.9){
            pan(5, 0);
        }
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        zoom(mwe.getPreciseWheelRotation() / 100);
    }
    
    public static File createNewImageFile(BufferedImage image){
        File f = null;
        JFileChooser cd = new JFileChooser();
        cd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        try {
            if(cd.showDialog(cd, "Select a location to place the new map file") == JFileChooser.APPROVE_OPTION){
                f = new File(cd.getSelectedFile().getPath() + File.separator + "mapImage" + System.currentTimeMillis() + ".png");
                ImageIO.write(image, "png", f);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return f;
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //System.out.println(clipX + ", " + clipY + ", " + clipW + ", " + clipH);
        g.drawImage(buff.getSubimage(clipX, clipY, clipW, clipH), 0, 0, (int)(getWidth() * aspectRatio), getHeight(), this);
        
    }
}