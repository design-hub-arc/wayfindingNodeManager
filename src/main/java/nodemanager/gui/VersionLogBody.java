package nodemanager.gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.io.IOException;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTextField;
import nodemanager.io.GoogleDriveUploader;
import nodemanager.io.VersionLog;

/**
 * Allows the user to manage the version log,
 * manually adding or removing manifests.
 * We will need to find some way to make sure this is
 * secure: we don't want people messing with our data.
 * 
 * @author Matt Crow
 */
public class VersionLogBody extends Container implements MouseListener{
    VersionLog v;
    public VersionLogBody() {
        super();
        
        v = new VersionLog();
        v.download();
        
        refresh();
    }
    
    private void refresh(){
        this.removeAll();
        String[] rows = v.getContentsToWrite().split(System.lineSeparator());
        
        setLayout(new GridLayout(rows.length, rows[0].split(",").length));
        String[] data;
        JTextField j;
        boolean firstRow = true;
        for(String row : rows){
            data = row.split(",", -1); // -1 allows it to include empty cells
            for(String datum : data){
                j = new JTextField(datum);
                j.setBackground(Color.LIGHT_GRAY);
                if(!firstRow && datum.trim().length() > 0){
                    try{
                        j.setText(GoogleDriveUploader.getFileName(datum));
                        j.setBackground(Color.GREEN);
                    }catch(IOException e){
                        j.setText("Google drive cannot find file by id: " + datum);
                        j.setBackground(Color.RED);
                    }
                }
                j.addMouseListener(this);
                j.setEditable(false);
                add(j);
            }
            firstRow = false;
        }
        revalidate();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        //find which was clicked
        //if it is in the first row, ask if they want to delete that version. If so, delete and refresh
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}
}
