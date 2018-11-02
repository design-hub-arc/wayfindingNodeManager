package nodemanager.gui;
import javax.swing.JComponent;

/**
 * Scale is used to scale a collection of points to any other scale.
 * When scaling points, the program states that the upper-leftmost point in the collection is positioned at (0, 0) on the scaling destination.
 * similarly, the point (sourceWidth, sourceHeight) is positioned at (destinationWidth, destinationHeight).
 * Given this relationship, each point in the collection is a set percentage of the way across and down the scaling destination's coordinate system.
 */

public class Scale {
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private double sourceWidth;
    private double sourceHeight;
    
    private double destinationWidth;
    private double destinationHeight;
    
    /**
     * @param minx : the horizontal component of the upper-leftmost point of the item(s) you want to scale
     * @param miny : the vertical component of the upper-leftmost point of the item(s) you want to scale
     * @param maxx : the horizontal component of the lower-rightmost point of the item(s) you want to scale
     * @param maxy : the vertical component of the lower-rightmost point of the item(s) you want to scale
     */
    public Scale(double minx, double miny, double maxx, double maxy){
        rescale(minx, miny, maxx, maxy); 
    }
    public Scale(){
        this(0.0, 0.0, 0.0, 0.0);
    }
    
    /**
     * @param minx : the horizontal component of the upper-leftmost point of the item(s) you want to scale
     * @param miny : the vertical component of the upper-leftmost point of the item(s) you want to scale
     * @param maxx : the horizontal component of the lower-rightmost point of the item(s) you want to scale
     * @param maxy : the vertical component of the lower-rightmost point of the item(s) you want to scale
     */
    public void rescale(double minx, double miny, double maxx, double maxy){
        minX = minx;
        minY = miny;
        maxX = maxx;
        maxY = maxy;
        sourceWidth = maxX - minX;
        sourceHeight = maxY - minY;
    }
    
    /**
     * @param j : The JLabel you want to scale points to 
     */
    public void setSource(JComponent j){
        setSize(j.getWidth(), j.getHeight());
    }
    
    /**
     * @param w : the sourceWidth to scale points to
     * @param h : the sourceHeight to scale points to
     */
    public void setSize(int w, int h){
        destinationWidth = w;
        destinationHeight = h;
    }
    
    /**
     * @param x : the horizontal component of a point within the source point collection you want to scale
     * @return the equivalent x-coordinate on the destination plane
     */
    public int x(double x){
        double percLeft = (x - minX) / sourceWidth;
        return (int)(percLeft * destinationWidth);
    }
    
    /**
     * @param y : the vertical component of a point within the source point collection you want to scale
     * @return the equivalent y-coordinate on the destination plane
     */
    public int y(double y){
        double percDown = (y - minY) / sourceHeight;
        return (int)(percDown * destinationHeight);
    }
    
    /**
     * 
     * @param x : an x-coordinate on the destination plane
     * @return the corresponding vertical component of a point on the source plane
     */
    public double inverseX(int x){
        return minX + (sourceWidth * x) / destinationWidth;
    }
    
    /**
     * 
     * @param y : an y-coordinate on the destination plane
     * @return the corresponding horizontal component of a point on the source plane
     */
    public double inverseY(int y){
        return minY + (sourceHeight * y) / destinationHeight;
    }
}