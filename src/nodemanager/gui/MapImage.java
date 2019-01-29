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

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */

/**
* MapImage is used to render an image,
* as well as proved a scale to resize points drawn onto it.
*
* Upon creating a MapImage, you need to call setImage on a file,
* then scaleTo a set of coordinates
* 
* Displays a portion of the image it is displaying by taking a subsection of that image, a 'clip',
* then displaying that clip over this component using paintcomponent
*/
public class MapImage extends JLabel implements MouseListener, MouseMotionListener, MouseWheelListener{
    private BufferedImage buff;
    private final Scale scaler;
    private final HashMap<Integer, NodeIcon> nodeIcons; // the node icons this is displaying
    
    private double zoom;
    
    /*
    these are the dimensions of the clip
    of the map image that the program displays
    */
    private int clipX;
    private int clipY;
    
    private double aspectRatio;
    
    private NodeIcon hoveringOver;
    private MapKeyAdapter key;
    
    private ActionListener updateKeys;
    private javax.swing.Timer update;
    
    /**
     * Initially, does not have any image or scale.
     */
    public MapImage(){
        super();
        setVisible(true);
        scaler = new Scale();
        nodeIcons = new HashMap<>();
        
        zoom = 1.0;
        
        clipX = 0;
        clipY = 0;
        
        hoveringOver = null;
        
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
        
        key = new MapKeyAdapter();
        addKeyListener(key);
        
        updateKeys = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.out.println(key.getKeyPressed());
                switch(key.getKeyPressed()){
                    case KeyEvent.VK_UP:
                        pan(0, -5);
                        break;
                        
                    case KeyEvent.VK_DOWN:
                        pan(0, 5);
                        break;
                        
                    case KeyEvent.VK_LEFT:
                        pan(-5, 0);
                        break;
                        
                    case KeyEvent.VK_RIGHT:
                        pan(5, 0);
                        break;
                        
                    default:
                        //do nothing
                }
                repaint();
            }
        };
        
        JComponent head = this;
        /*
        while(head.getParent() != null){
            head = (JComponent)head.getParent();
        }*/
        
        
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "panUp");
        getActionMap().put("panUp", new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                pan(-5, 0);
                System.out.println("up");
            }
        });
        
        System.out.println(Arrays.toString(getInputMap(WHEN_IN_FOCUSED_WINDOW).keys()));
        getActionMap().get("panUp").actionPerformed(new ActionEvent(this, 0, "whatever"){});
        
        update = new javax.swing.Timer(500, updateKeys);
        update.setRepeats(true);
        //update.start();
    }
    
    /**
     * Converts a click on the MapImage to where the user would be clicking on the actual image
     * @param x mouse click x coordinate
     * @return the x coordinate on the image where the user would have clicked
     */
    private int translateClickX(int x){
        return (int) ((x + clipX) / zoom);
    }
    
    /**
     * Converts a click on the MapImage to where the user would be clicking on the actual image
     * @param y mouse click y coordinate
     * @return the y coordinate on the image where the user would have clicked
     */
    private int translateClickY(int y){
        return (int) ((y + clipY) / zoom);
    }
    
    /**
     * Adds a Node's NodeIcon to the MapImage,
     * then positions it.
     * @param n the Node to add to the MapImage
     */
    public void addNode(Node n){
        NodeIcon ni = n.getIcon();
        ni.scaleTo(scaler);
        ni.setHost(this);
        nodeIcons.put(n.id, ni);
        repaint();
    }
    
    /**
     * Removes a node icon from the map.
     * Does not delete the node
     * @param n the Node whose icon we want to remove
     */
    public void removeNode(Node n){
        nodeIcons.remove(n.id);
        repaint();
    }
    
    /**
     * Removes all NodeIcons from this element.
     */
    public void removeAllNodes(){
        nodeIcons.clear();
        repaint();
    }
    
    /**
     * resets the position of each NodeIcon.
     */
    private void resizeNodeIcons(){
        nodeIcons.values().forEach(n -> n.scaleTo(scaler));
        repaint();
    }
    
    /**
     * Moves the clip of the image this element is displaying.
     * Automatically prevents going out of the image's bounds
     * @param x the amount the clip is moved to the right
     * @param y the amount the clip is moved down
     */
    private void pan(int x, int y){
        clipX += x;
        clipY += y;
        repaint();
    }
    
    /**
     * Reduces the width and height of the clip area of the image. 
     * Since the clip is still rendered at the same size,
     * it has to fill the same area, but with a smaller image,
     * giving the illusion of zooming in.
     * @param perc the percentage to zoom in. Negative to zoom in.
     */
    private void zoom(double perc){
        zoom -= perc;
        repaint();
    }
    
    /**
     * Called whenever this component is resized.
     * Repositions all the NodeIcons.
     */
    private void resize(){
        zoom = 1.0;
        scaler.setSize(buff.getWidth(), buff.getHeight());
        resizeNodeIcons();
    }
    
    /**
     * Changes the image displayed by this component,
     * resetting the clip info in the process
     * @param bi the image to display
     */
    public void setImage(BufferedImage bi){
        buff = bi;
        aspectRatio = 1.0 * buff.getWidth() / buff.getHeight();
        clipX = 0;
        clipY = 0;
        resize();
    }
    
    /**
     * Scales the coordinate conversion to two points
     * @param x1 the x coordinate of the upper left point
     * @param y1 the y coordinate of the upper left point
     * @param x2 the x coordinate of the lower right point
     * @param y2 the y coordinate of the lower right point
     */
    public void scaleTo(double x1, double y1, double x2, double y2){
        scaler.rescale(x1, y1, x2, y2);
    }
    
    /**
     * Currently doesn't work with resized image?
     */
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
    
    
    /**
     * @throws NullPointerException if the mouse isn't over a node.
     * @return the NodeIcon the mouse is over
     */
    private NodeIcon hoveredNodeIcon(int mouseX, int mouseY) throws NullPointerException{
        int x = translateClickX(mouseX);
        int y = translateClickY(mouseY);
        return nodeIcons.values().stream().filter(icon -> {
            return icon.isIn(x, y);
        }).findFirst().orElse(null);
    }

    
    @Override
    public void mouseMoved(MouseEvent me) {
        
        NodeIcon oldOver = hoveringOver;
        hoveringOver = hoveredNodeIcon(me.getX(), me.getY());
        
        if(oldOver != hoveringOver){
            if(hoveringOver != null){
                hoveringOver.mouseEntered(me);
            }
            if(oldOver != null){
                oldOver.mouseExited(me);
            }
        }
        
        switch(Session.mode){
            case MOVE:
                // shows where the selected node will be repositioned when the user clicks
                Session.selectedNode.getIcon().setPos(translateClickX(me.getX()), translateClickY(me.getY()));
                break;
            case RESCALE_UL:
                double shiftX = translateClickX(me.getX());
                double shiftY = translateClickY(me.getY());
                double baseX;
                double baseY;
                for(NodeIcon ni : nodeIcons.values()){
                    baseX = scaler.x(ni.node.rawX);
                    baseY = scaler.y(ni.node.rawY);
                    ni.setPos((int)(baseX + shiftX), (int)(baseY + shiftY));
                }
                break;
            case RESCALE_LR:
                scaler.setSize(translateClickX(me.getX() - Session.newMapX), translateClickY(me.getY() - Session.newMapY));
                resizeNodeIcons();
                break;
        }//end switch
        
        //pan if the user moves the mouse within 10% of the edge of the displayed clip
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
    public void mouseClicked(MouseEvent me) {
        if(hoveringOver != null){
            hoveringOver.mouseClicked(me);
        }
        
        switch(Session.mode){
            case ADD:
                //adds a Node where the user clicks
                Node n = new Node(
                        (int)scaler.inverseX(translateClickX(me.getX())), 
                        (int)scaler.inverseY(translateClickY(me.getY()))
                );
                addNode(n);
                repaint();
                Session.mode = Mode.NONE;
                break;
            case MOVE:
                //repositions a Node to where the user clicks
                //Session.selectedNode.getIcon().setPos(translateClickX(me.getX()), translateClickY(me.getY()));
                Session.mode = Mode.NONE;
                break;
            case RESCALE_UL:
                // sets the upper-left corner of the new map image clip
                Session.mode = Mode.RESCALE_LR;
                Session.newMapX = Node.get(-1).getIcon().getX();
                Session.newMapY = Node.get(-1).getIcon().getY();
                JOptionPane.showMessageDialog(null, "Position the upper left corner of node -2 at the lower right corner of where you want to crop");
                break;
            case RESCALE_LR:
                // sets the lower-right corner of the new map image clip
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
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
    

    @Override
    public void mouseDragged(MouseEvent me) {}


    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        zoom(mwe.getPreciseWheelRotation() / 100);
    }
    
    /**
     * Saves a BufferedImage to a file
     * @param image the BufferedImage to save
     * @return a File containing the new image
     */
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
    /**
     * Renders a clip of the image. 
     * Note that this means the displayed image isn't the actual component
     * @param g 
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        g.translate(-clipX, -clipY);
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.scale(zoom, zoom);
        
        g2d.drawImage(buff, 0, 0, this);
        nodeIcons.values().stream().forEach(icon -> icon.draw(g2d));
    }
}

class MapKeyAdapter extends KeyAdapter{
    private int pressed;
    
    public MapKeyAdapter(){
        super();
        pressed = 0;
    }
    
    @Override
    public void keyPressed(KeyEvent e){
        pressed = e.getKeyCode();
        System.out.println("Pressed: " + pressed);
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        pressed = 0;
    }
    
    public int getKeyPressed(){
        return pressed;
    }
}