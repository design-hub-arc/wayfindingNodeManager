package nodemanager.gui;

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
    private int shiftX; //shifts the origin of the destination plane
    private int shiftY;
    
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
        shiftX = 0;
        shiftY = 0;
    }
    
    /**
     * @param w : the destination width to scale points to
     * @param h : the destination height to scale points to
     */
    public void setSize(int w, int h){
        destinationWidth = w;
        destinationHeight = h;
    }
    
    /**
     * Sets where all points should draw from
     * @param x the value added to each point's x coordinate
     * @param y the value added to each point's y coordinate
     */
    public void setOrigin(int x, int y){
        shiftX = x;
        shiftY = y;
    }
    
    /**
     * @param x : the horizontal component of a point within the source point collection you want to scale
     * @return the equivalent x-coordinate on the destination plane
     */
    public int x(double x){
        double percLeft = (x - minX) / sourceWidth;
        return (int)(percLeft * destinationWidth) + shiftX;
    }
    
    /**
     * @param y : the vertical component of a point within the source point collection you want to scale
     * @return the equivalent y-coordinate on the destination plane
     */
    public int y(double y){
        double percDown = (y - minY) / sourceHeight;
        return (int)(percDown * destinationHeight) + shiftY;
    }
    
    /**
     * 
     * @param x : an x-coordinate on the destination plane
     * @return the corresponding vertical component of a point on the source plane
     */
    public double inverseX(int x){
        return minX + (sourceWidth * (x - shiftX)) / destinationWidth;
    }
    
    /**
     * 
     * @param y : an y-coordinate on the destination plane
     * @return the corresponding horizontal component of a point on the source plane
     */
    public double inverseY(int y){
        return minY + (sourceHeight * (y - shiftY)) / destinationHeight;
    }
    
    //testing function
    public static void main(String[] args){
        Scale s = new Scale(0, 5, 290, 100);
        s.setSize(500, 37);
        s.setOrigin(50, 50);
        
        int val;
        for(int i = 0; i < 999999; i++){
            s.setOrigin(i, i);
            val = s.x(315);
            if(s.inverseX(val) - 315 > 5){
                System.out.println("Fail: " + 315 + " " + val);
            }
        }
        System.out.println("done");
    }
}