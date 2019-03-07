package nodemanager.gui;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.event.*;
import static java.lang.System.out;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.stream.ImageInputStream;
import nodemanager.node.Node;
import nodemanager.*;
import nodemanager.events.*;

/**
 * @author Matt Crow (greengrappler12@gmail.com)
 */
/**
 * MapImage is used to render an image, as well as proved a scale to resize
 * points drawn onto it.
 *
 * Upon creating a MapImage, you need to call setImage on a file, then scaleTo a
 * set of coordinates
 *
 */
public class MapImage extends JLabel{

    private BufferedImage buff;
    private final Scale scaler;
    private final HashMap<Integer, NodeIcon> nodeIcons; // the node icons this is displaying

    /*
    these are the dimensions of the clip
    of the map image that the program displays
     */
    private int clipX;
    private int clipY;
    private double zoom;

    private NodeIcon hoveringOver;

    private int panSpeed; //how fast the map pans
    private double zoomSpeed; //how fast the map zooms
    
    /**
    The way this component handles mouse events:
     *
     * Whenever the user moves the mouse, a MouseAdapter calls its mouseMoved
     * method, checking what Session.mode is, then calling the EasyMouseListener
     * stored in mouseMoveActions.get(Session.mode).
     *
     * Functions similarly for mouse clicking.
     *
     * To add your own mouse reactions, use
     * <br>      
     * {@code
     * mouseMoveActions.put(Mode, (me) -> { STUFF }); }
     * <br>
     */
    private final HashMap<Mode, EasyMouseListener> mouseMoveActions; //responses to mouse movement
    private final HashMap<Mode, EasyMouseListener> mouseClickActions; //responses to mouse click

    /**
     * Initially, does not have any image or scale.
     */
    public MapImage() {
        super();
        setVisible(true);
        scaler = new Scale();
        nodeIcons = new HashMap<>();

        zoom = 1.0;

        clipX = 0;
        clipY = 0;

        hoveringOver = null;

        panSpeed = 5;
        zoomSpeed = 0.01;

        setBackground(Color.BLACK);
        
        mouseMoveActions = new HashMap<>();
        mouseClickActions = new HashMap<>();
        
        registerControls();
        
        Session.map = this;
    }

    
    /**
     * Adds the controls to the map image, 
     * and registers all the mouse listeners
     */
    private void registerControls() {
        mouseMoveActions.put(Mode.MOVE, (me) -> {
            Session.selectedNode.getIcon().setPos(translateClickX(me.getX()), translateClickY(me.getY()));     
        });
        mouseMoveActions.put(Mode.RESCALE_UL, (me) -> {
            double shiftX = translateClickX(me.getX());
            double shiftY = translateClickY(me.getY());
            scaler.setOrigin((int)shiftX, (int)shiftY);
            resizeNodeIcons();
        });
        mouseMoveActions.put(Mode.RESCALE_LR, (me) -> {
            scaler.setSize(translateClickX(me.getX() - Session.newMapX), translateClickY(me.getY() - Session.newMapY));
            resizeNodeIcons();
        });
        
        mouseClickActions.put(Mode.ADD, (me) -> {
            //adds a Node where the user clicks
            Node n = new Node(
                    (int) scaler.inverseX(translateClickX(me.getX())),
                    (int) scaler.inverseY(translateClickY(me.getY()))
            );
            addNode(n);
            Session.logAction(new NodeCreateEvent(n, this));
            repaint();
            Session.setMode(Mode.NONE);
        });
        
        mouseClickActions.put(Mode.MOVE, (me) -> {
            Session.selectedNode.getIcon().respositionNode();
            Session.setMode(Mode.NONE);
        });
        
        mouseClickActions.put(Mode.RESCALE_UL, (me) -> {
            // sets the upper-left corner of the new map image clip
            Session.setMode(Mode.RESCALE_LR);
            Session.newMapX = Node.get(-1).getIcon().getX();
            Session.newMapY = Node.get(-1).getIcon().getY();
            
            JOptionPane.showMessageDialog(null, "Position the upper left corner of node -2 at the lower right corner of where you want to crop");
        });
        
        mouseClickActions.put(Mode.RESCALE_LR, (me) -> {
            // sets the lower-right corner of the new map image clip
            Session.setMode(Mode.NONE);
            Session.newMapWidth = Node.get(-2).getIcon().getX() - Session.newMapX;
            Session.newMapHeight = Node.get(-2).getIcon().getY() - Session.newMapY;

            int[] clip = new int[]{Session.newMapX, Session.newMapY, Session.newMapWidth, Session.newMapHeight};

            if (clip[0] < 0) {
                clip[0] = 0;
            }
            if (clip[1] < 0) {
                clip[1] = 0;
            }
            if (clip[2] > buff.getWidth() - clip[0]) {
                clip[2] = buff.getWidth() - clip[0];
            }
            if (clip[3] > buff.getHeight() - clip[1]) {
                clip[3] = buff.getHeight() - clip[1];
            }

            out.println();
            for (int i : clip) {
                out.print(i + " ");
            }
            
            Session.newMapX = 0;
            Session.newMapY = 0;
            scaler.setOrigin(0, 0);
            
            Session.logAction(new MapResizeEvent(this, buff, buff.getSubimage(clip[0], clip[1], clip[2], clip[3])));
            
            setImage(buff.getSubimage(clip[0], clip[1], clip[2], clip[3]));
        });
        
        
        
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent me) {
                NodeIcon oldOver = hoveringOver;
                hoveringOver = hoveredNodeIcon(me.getX(), me.getY());

                if (oldOver != hoveringOver) {
                    if (hoveringOver != null) {
                        hoveringOver.mouseEntered(me);
                    }
                    if (oldOver != null) {
                        oldOver.mouseExited(me);
                    }
                }

                mouseMoveActions.getOrDefault(Session.getMode(), (me2) -> {}).mouseAction(me);
                
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent me){
                if (hoveringOver != null) {
                    hoveringOver.mouseClicked(me);
                }
                mouseClickActions.getOrDefault(Session.getMode(), (me2) -> {}).mouseAction(me);
            }
        };
        
        addMouseListener(ma);
        addMouseMotionListener(ma);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                resize();
            }
        });

        /*
        For some reason, VK_UP & co don't work
         */
        Session.registerControl(KeyEvent.VK_W, () -> pan(0, -panSpeed), "pan the map up");
        Session.registerControl(KeyEvent.VK_S, () -> pan(0, panSpeed), "pan the map down");
        Session.registerControl(KeyEvent.VK_A, () -> pan(-panSpeed, 0), "pan the map left");
        Session.registerControl(KeyEvent.VK_D, () -> pan(panSpeed, 0), "pan the map right");
        Session.registerControl(KeyEvent.VK_Q, () -> zoom(-zoomSpeed), "zoom in");
        Session.registerControl(KeyEvent.VK_E, () -> zoom(zoomSpeed), "zoom out");
    }

    /**
     * Converts a click on the MapImage to where the user would be clicking on
     * the actual image
     *
     * @param x mouse click x coordinate
     * @return the x coordinate on the image where the user would have clicked
     */
    private int translateClickX(int x) {
        return (int) ((x + clipX) / zoom);
    }

    /**
     * Converts a click on the MapImage to where the user would be clicking on
     * the actual image
     *
     * @param y mouse click y coordinate
     * @return the y coordinate on the image where the user would have clicked
     */
    private int translateClickY(int y) {
        return (int) ((y + clipY) / zoom);
    }

    /**
     * Adds a Node's NodeIcon to the MapImage, then positions it.
     *
     * @param n the Node to add to the MapImage
     */
    public void addNode(Node n) {
        NodeIcon ni = n.getIcon();
        ni.scaleTo(scaler);
        ni.setHost(this);
        nodeIcons.put(n.id, ni);
        repaint();
    }

    /**
     * Removes a node icon from the map. Does not delete the node
     *
     * @param n the Node whose icon we want to remove
     */
    public void removeNode(Node n) {
        nodeIcons.remove(n.id);
        repaint();
    }

    /**
     * Removes all NodeIcons from this element.
     */
    public void removeAllNodes() {
        nodeIcons.clear();
        repaint();
    }

    /**
     * resets the position of each NodeIcon.
     */
    private void resizeNodeIcons() {
        nodeIcons.values().forEach(n -> {
            n.scaleTo(scaler);
        });
        repaint();
    }

    /**
     * Moves the clip of the image this element is displaying. Automatically
     * prevents going out of the image's bounds
     *
     * @param x the amount the clip is moved to the right
     * @param y the amount the clip is moved down
     */
    private void pan(int x, int y) {
        clipX += x;
        clipY += y;
        repaint();
    }

    /**
     * Sets how fast the map will pan
     *
     * @param speed the amount, in pixels, the map moves when the user presses a
     * directional key
     */
    public void setPanSpeed(int speed) {
        panSpeed = speed;
    }

    /**
     * changes the zoom level of the image
     *
     * @param perc the percentage to zoom in. Negative to zoom in.
     */
    private void zoom(double perc) {
        zoom -= perc;
        repaint();
    }

    /**
     * changes how fast the map zooms in and out. 0.01 corresponds to a 1% zoom
     * out
     *
     * @param speed the amount the map will zoom each time the user presses the
     * zoom key
     */
    public void setZoomSpeed(double speed) {
        zoomSpeed = speed;
    }

    /**
     * Called whenever this component is resized. Repositions all the NodeIcons.
     */
    private void resize() {
        zoom = 1.0;
        scaler.setSize(buff.getWidth(), buff.getHeight());
        resizeNodeIcons();
    }

    /**
     * Changes the image displayed by this component, resetting the clip info in
     * the process
     *
     * @param bi the image to display
     */
    public void setImage(BufferedImage bi) {
        buff = bi;
        clipX = 0;
        clipY = 0;
        resize();
    }

    /**
     * Scales the coordinate conversion to two points
     *
     * @param x1 the x coordinate of the upper left point
     * @param y1 the y coordinate of the upper left point
     * @param x2 the x coordinate of the lower right point
     * @param y2 the y coordinate of the lower right point
     */
    public void scaleTo(double x1, double y1, double x2, double y2) {
        scaler.rescale(x1, y1, x2, y2);
    }
    
    
    /**
     * Invoked by ImportMenu after importing the node file.
     * Sets the node icons on the map image to match the newly imported nodes.
     * 
     * Move this to map later?
     */
    public void refreshNodes() {
        removeAllNodes();
        scaleTo(Node.get(-1).getX(), Node.get(-1).getY(), Node.get(-2).getX(), Node.get(-2).getY());
        Node.getAll().forEach((n) -> addNode(n));
        revalidate();
        repaint();
    }

    /**
     * exports the map image to a directory
     * @param name what to append to the beginning of the file name: nameMapImage.png 
     * @param path the directory to save the image to
     * @return the newly created file
     */
    public File saveImage(String name, String path) {
        File f = null;
        try {
            f = new File(path + File.separator + "MapImage.png");
            ImageIO.write(buff, "png", f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return f;
    }
    
    /**
     * Creates a temporary file with the given name 
     * @param name appended to the beginning of the file name: nameMapImage.png
     * @return the newly created file
    */
    public File saveImage(String name){
        File f = null;
        try {
            f = new File(name + "MapImage.png");
            f.deleteOnExit();
            ImageIO.write(buff, "png", f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return f;
    }
    
    
    
    
    
    public String getImageAsString() throws IOException{
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(buff, "png", bytes);
        bytes.flush();
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }
    
    //not working. How convert inputstream to something base64 can decode?
    public void setImage(InputStream s){
        /*
        try{
            System.out.println(ImageIO.read(new BufferedInputStream(s)));
            byte[] bytes = Base64.getDecoder().decode(s));
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            setImage(ImageIO.read(stream));//new ByteArrayInputStream(Base64.getDecoder().decode(s))));
        } catch(IOException e){
            e.printStackTrace();
        }
        */
    }
    
    

    /**
     * @throws NullPointerException if the mouse isn't over a node.
     * @return the NodeIcon the mouse is over
     */
    private NodeIcon hoveredNodeIcon(int mouseX, int mouseY) throws NullPointerException {
        int x = translateClickX(mouseX);
        int y = translateClickY(mouseY);
        return nodeIcons.values().stream().filter(icon -> {
            return icon.isIn(x, y);
        }).findFirst().orElse(null);
    }
    
    @Override
    /**
     * Renders a clip of the image. Note that this means the displayed image
     * isn't the actual component
     *
     * @param g
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.translate(-clipX, -clipY);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.scale(zoom, zoom);

        g2d.drawImage(buff, 0, 0, this);
        nodeIcons.values().stream().forEach(icon -> icon.draw(g2d));
    }
    
    private interface EasyMouseListener{
        public void mouseAction(MouseEvent e);
    }
}
