package nodemanager.gui.editPage.mapComponents;

import java.awt.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.*;
import nodemanager.model.Node;
import nodemanager.*;
import nodemanager.events.*;
import nodemanager.model.Graph;

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
public class MapImage extends JLabel implements MouseListener, MouseMotionListener {
    private Graph representedGraph;
    
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

        setBackground(Color.BLACK);
        
        setFocusable(true);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ce) {
                resize();
            }
        });
        
        addMouseListener(this);
        addMouseMotionListener(this);
        MapPanner panner = new MapPanner(this);
        addMouseListener(panner);
        addMouseMotionListener(panner);
        addMouseWheelListener(new MapZoomer(this));
    }
    
    public final Graph getGraph(){
        return representedGraph;
    }
    
    public final void renderGraph(Graph g){
        representedGraph = g;
        removeAllNodes();
        setImage(g.getMapImage());
        scaleTo(g.getNodeById(-1).getX(), g.getNodeById(-1).getY(), g.getNodeById(-2).getX(), g.getNodeById(-2).getY());
        g.getAllNodes().forEach(this::addNode);
        resizeNodeIcons();
    }
    
    public final NodeIcon getIcon(int id){
        return this.nodeIcons.get(id);
    }
    
    public final Point mouseClickToNodeSpace(Point mouseClick){
        return new Point(
            (int)scaler.inverseX(translateClickX(mouseClick.x)),
            (int)scaler.inverseY(translateClickY(mouseClick.y))
        );
    }
    
    /**
     * Converts a click on the MapImage to where the user would be clicking on
     * the actual image
     *
     * @param x mouse click x coordinate
     * @return the x coordinate on the image where the user would have clicked
     */
    public final int translateClickX(int x) {
        return (int) ((x + clipX) / zoom);
    }

    /**
     * Converts a click on the MapImage to where the user would be clicking on
     * the actual image
     *
     * @param y mouse click y coordinate
     * @return the y coordinate on the image where the user would have clicked
     */
    public final int translateClickY(int y) {
        return (int) ((y + clipY) / zoom);
    }

    /**
     * Adds a Node's NodeIcon to the MapImage, then positions it.
     *
     * @param n the Node to add to the MapImage
     */
    public void addNode(Node n) {
        NodeIcon ni = new NodeIcon(n);
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
    protected void pan(int x, int y) {
        clipX += x;
        clipY += y;
        repaint();
    }

    /**
     * changes the zoom level of the image
     *
     * @param perc the percentage to zoom in. Negative to zoom in.
     */
    protected void zoom(double perc) {
        zoom -= perc;
        repaint();
    }

    /**
     * Called whenever this component is resized. Repositions all the NodeIcons.
     */
    private void resize() {
        zoom = 1.0;
        if(buff != null){
            scaler.setSize(buff.getWidth(), buff.getHeight());
        }
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
    
    public final void setScaleOrigin(int clickX, int clickY){
        scaler.setOrigin(
            translateClickX(clickX), 
            translateClickY(clickY)
        );
        resizeNodeIcons();
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
     * 
     * @return the current image this is displaying
     */
    public final BufferedImage getImage(){
        return buff;
    }
    
    
    /**
     * @throws NullPointerException if the mouse isn't over a node.
     * @return the NodeIcon the mouse is over
     */
    public final NodeIcon hoveredNodeIcon(int mouseX, int mouseY) throws NullPointerException {
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

        if(buff == null){
            g2d.drawString("Import an image to show it here", getWidth() / 2, getHeight() / 2);
        } else {
            g2d.drawImage(buff, 0, 0, this);
        }
        // draw icons above the connections
        nodeIcons.values().stream().forEach((icon) -> icon.drawAllLinks(g2d));
        nodeIcons.values().stream().forEach((icon) -> icon.draw(g2d));
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        
        NodeManager.getInstance().mapClicked(this, me); // for now
        
        if (hoveringOver != null) {
            hoveringOver.mouseClicked(me);
        }
        
        switch(Session.getMode()){
            /*
            case RESCALE_UL: {
                // sets the upper-left corner of the new map image clip
                Session.setMode(Mode.RESCALE_LR);
                NodeIcon icon = getIcon(-1);
                Session.newMapX = icon.getX();
                Session.newMapY = icon.getY();
                break;
            }*/
            //case RESCALE_LR: {
                // sets the lower-right corner of the new map image clip
                //Session.setMode(Mode.NONE);
                //Session.newMapWidth = getIcon(-2).getX() - Session.newMapX;
                //Session.newMapHeight = getIcon(-2).getY() - Session.newMapY;

                //int[] clip = new int[]{Session.newMapX, Session.newMapY, Session.newMapWidth, Session.newMapHeight};
                /*
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

                Session.newMapX = 0;
                Session.newMapY = 0;*/
            /*
                scaler.setOrigin(0, 0);
                
                BufferedImage sub = buff.getSubimage(clip[0], clip[1], clip[2], clip[3]);
                NodeManager.getInstance().getLog().log(new MapResizeEvent(representedGraph, this, buff, sub));
                
                setImage(sub);
                representedGraph.setMapImage(sub);
                break;
            }*/
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
    
    @Override
    public void mouseDragged(MouseEvent e) {}

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

        switch(Session.getMode()){
            /*
            case RESCALE_UL: {
                double shiftX = translateClickX(me.getX());
                double shiftY = translateClickY(me.getY());
                scaler.setOrigin((int)shiftX, (int)shiftY);
                resizeNodeIcons();
                break;
            }*/
            /*
            case RESCALE_LR: {
                scaler.setSize(translateClickX(me.getX() - Session.newMapX), translateClickY(me.getY() - Session.newMapY));
                resizeNodeIcons();
                break;
            }*/
        }
    }
}
