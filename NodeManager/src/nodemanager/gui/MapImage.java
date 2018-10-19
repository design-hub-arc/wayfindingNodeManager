package nodemanager.gui;

import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import static java.lang.System.out;

public class MapImage extends JLabel{
    private BufferedImage buff;
    private Scale scaler;
    
    public MapImage(){
        super();
        setVisible(true);
        scaler = new Scale();
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
    
    public void displayData(){
        for(Component c : getComponents()){
            out.println(c.getX());
        }
    }
}
