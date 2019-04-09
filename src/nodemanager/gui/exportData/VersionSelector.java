package nodemanager.gui.exportData;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import javax.swing.*;
import nodemanager.io.VersionLog;

/**
 *
 * @author Matt Crow
 */
public class VersionSelector extends JComponent{
    public static final String NEW_TYPE = "New type";
    
    private final VersionLog v;
    private final JComboBox<String> selectType;
    private final JTextArea msg;
    
    public VersionSelector(){
        setLayout(new GridLayout(3, 1));
        
        selectType = new JComboBox<>();
        selectType.addItem(NEW_TYPE);
        selectType.addItemListener((ItemEvent e)->{
            if(selectType.getSelectedItem().equals(NEW_TYPE)){
                String name = JOptionPane.showInputDialog("Enter the name of this new version:");
                
                if(selectType.getItemCount() > 1){
                    selectType.setSelectedIndex(0);
                }
                addOption(name);
                selectType.setSelectedItem(name);
                revalidate();
                repaint();
            }
        });
        
        msg = new JTextArea("Please hold while I download the version log...");
        msg.setEditable(false);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(msg);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(new JLabel("Select which version of wayfinding this is for"));
        add(selectType);
        add(scroll); 
        
        v = new VersionLog();
        v.download().addOnSucceed((stream)->{
            for(String option : v.getTypes()){
                addOption(option);
                selectType.setSelectedIndex(0);
                msg.setText("Select version");
            }
        });
    }
    
    
    public void addOption(String opt){
        selectType.insertItemAt(opt, selectType.getItemCount() - 1);
    }
    
    public String getSelectedType(){
        return (String)selectType.getSelectedItem();
    }
}
