package nodemanager.gui;

import java.awt.*;
import javax.swing.*;

//might be able to use to scale components
public class MapGrid extends JComponent{
    int rows;
    int cols;
    public MapGrid(int r, int c){
        super();
        rows = r;
        cols = c;
        setLayout(new GridLayout(r, c));
        
        for(int i = 0; i < r; i++){
            for(int j = 0; j < c; j++){
                //but this...
                add(new Component(){}, i, j);
            }
        }
        
        setVisible(true);
    }
}
