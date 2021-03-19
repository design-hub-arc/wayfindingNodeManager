package nodemanager.gui.editPage.mapComponents;

/**
 * Scale is used to scale a collection of points to any other scale.
 * (namely, Nodes to a map image)
 * When scaling points, the program states that the upper-leftmost point in the 
 * collection is positioned at (0, 0) on the scaling destination. Similarly, the 
 * point (nodeSpaceWidth, nodeSpaceHeight) is positioned at (mapWidth, mapHeight).
 * Given this relationship, each point in the collection is a set percentage of 
 * the way across and down the scaling destination's coordinate system.
 */

public class Scale {
    private double nodeSpaceMinX;
    private double nodeSpaceMinY;
    private double nodeSpaceMaxX;
    private double nodeSpaceMaxY;
    private double nodeSpaceWidth;
    private double nodeSpaceHeight;
    
    private double mapWidth;
    private double mapHeight;
    private int shiftX; //shifts the origin of the destination plane
    private int shiftY;
    
    /**
     * @param minx : the horizontal component of the upper-leftmost point of the nodes you want to scale
     * @param miny : the vertical component of the upper-leftmost point of the nodes you want to scale
     * @param maxx : the horizontal component of the lower-rightmost point of the nodes you want to scale
     * @param maxy : the vertical component of the lower-rightmost point of the nodes you want to scale
     */
    public Scale(double minx, double miny, double maxx, double maxy){
        rescale(minx, miny, maxx, maxy); 
    }
    public Scale(){
        this(0.0, 0.0, 0.0, 0.0);
    }
    
    /**
     * @param minx : the horizontal component of the upper-leftmost point of the nodes you want to scale
     * @param miny : the vertical component of the upper-leftmost point of the nodes you want to scale
     * @param maxx : the horizontal component of the lower-rightmost point of the nodes you want to scale
     * @param maxy : the vertical component of the lower-rightmost point of the nodes you want to scale
     */
    public void rescale(double minx, double miny, double maxx, double maxy){
        nodeSpaceMinX = minx;
        nodeSpaceMinY = miny;
        nodeSpaceMaxX = maxx;
        nodeSpaceMaxY = maxy;
        nodeSpaceWidth = nodeSpaceMaxX - nodeSpaceMinX;
        nodeSpaceHeight = nodeSpaceMaxY - nodeSpaceMinY;
        shiftX = 0;
        shiftY = 0;
    }
    
    /**
     * @param w : the map width to scale points to
     * @param h : the map height to scale points to
     */
    public void setMapSize(int w, int h){
        mapWidth = w;
        mapHeight = h;
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
    public int nodeXToMapX(double x){
        double percLeft = (x - nodeSpaceMinX) / nodeSpaceWidth;
        return (int)(percLeft * mapWidth) + shiftX;
    }
    
    /**
     * @param y : the vertical component of a point within the source point collection you want to scale
     * @return the equivalent y-coordinate on the destination plane
     */
    public int nodeYToMapY(double y){
        double percDown = (y - nodeSpaceMinY) / nodeSpaceHeight;
        return (int)(percDown * mapHeight) + shiftY;
    }
    
    /**
     * 
     * @param x : an x-coordinate on the destination plane
     * @return the corresponding vertical component of a point on the source plane
     */
    public double mapXToNodeX(int x){
        return nodeSpaceMinX + (nodeSpaceWidth * (x - shiftX)) / mapWidth;
    }
    
    /**
     * 
     * @param y : an y-coordinate on the destination plane
     * @return the corresponding horizontal component of a point on the source plane
     */
    public double mapYToNodeY(int y){
        return nodeSpaceMinY + (nodeSpaceHeight * (y - shiftY)) / mapHeight;
    }
    
    //testing function
    public static void main(String[] args){
        Scale s = new Scale(0, 5, 290, 100);
        s.setMapSize(500, 37);
        s.setOrigin(50, 50);
        
        int val;
        for(int i = 0; i < 999999; i++){
            s.setOrigin(i, i);
            val = s.nodeXToMapX(315);
            if(s.mapXToNodeX(val) - 315 > 5){
                System.out.println("Fail: " + 315 + " " + val);
            }
        }
        System.out.println("done");
    }
}