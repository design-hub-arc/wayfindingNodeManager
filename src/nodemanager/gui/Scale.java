package nodemanager.gui;
import javax.swing.JLabel;

public class Scale {
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private double width;
    private double height;
    
    private double sourceWidth;
    private double sourceHeight;
    
    public Scale(double minx, double miny, double maxx, double maxy){
        rescale(minx, miny, maxx, maxy); 
    }
    public Scale(){
        this(0.0, 0.0, 0.0, 0.0);
    }
    
    public void rescale(double minx, double miny, double maxx, double maxy){
        minX = minx;
        minY = miny;
        maxX = maxx;
        maxY = maxy;
        width = maxX - minX;
        height = maxY - minY;
    }
    public void setSource(JLabel j){
        sourceWidth = j.getWidth();
        sourceHeight = j.getHeight();
    }
    public void setSize(double w, double h){
        sourceWidth = w;
        sourceHeight = h;
    }
    
    public int x(double x){
        double percLeft = (x - minX) / width;
        return (int)(percLeft * sourceWidth);
    }
    public double percX(double x){
        return (x - minX) / width;
    }
    
    public int y(double y){
        double percDown = (y - minY) / height;
        return (int)(percDown * sourceHeight);
    }
    public double percY(double y){
        return (y - minY) / height;
    }
    
    public double inverseX(int x){
        return minX + (width * x) / sourceWidth;
    }
    public double inverseY(int y){
        return minY + (height * y) / sourceHeight;
    }
    
    public int percWidth(int perc){
        /*
        @param perc : the percentage of the width you want
        ex. percWidth(10) will return 10% of the scale width
        @return the percentage of the scale width
        */
        return (int)((double)perc * sourceWidth / 100);
    }
    
    public int percHeight(int perc){
        /*
        @param perc : the percentage of the height you want
        ex. percWidth(10) will return 10% of the scale height
        @return the percentage of the scale width
        */
        return (int)((double)perc * sourceHeight / 100);
    }
}
