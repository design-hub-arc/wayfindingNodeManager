package nodemanager.modes;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import nodemanager.NodeManager;
import nodemanager.events.MapResizeEvent;
import nodemanager.gui.editPage.mapComponents.MapImage;

/**
 *
 * @author Matt
 */
public class ModeRescaleLowerRight extends AbstractMode {
    private final Point upperLeft;
    
    public ModeRescaleLowerRight(Point upperLeft) {
        super("Click on the lower right corner of where you want to crop");
        this.upperLeft = upperLeft;
    }

    @Override
    public AbstractMode mapImageClicked(MapImage mapImage, MouseEvent me) {
        int[] clip = new int[]{
            upperLeft.x,
            upperLeft.y,
            mapImage.translateClickX(me.getX()) - upperLeft.x,
            mapImage.translateClickY(me.getY()) - upperLeft.y
        }; 
        
        BufferedImage buff = mapImage.getImage();
        
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
        
        BufferedImage cropped = buff.getSubimage(
            clip[0],
            clip[1],
            clip[2],
            clip[3]
        );
        NodeManager.getInstance().getLog().log(new MapResizeEvent(mapImage.getGraph(), mapImage, buff, cropped));
        mapImage.setImage(cropped);
        mapImage.getGraph().setMapImage(cropped);
        mapImage.repaint();
        return new ModeNone();
    }

}
