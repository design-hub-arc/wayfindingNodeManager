package nodemanager.gui;

import java.awt.*;
import javax.swing.*;
import nodemanager.io.VersionLog;

/**
 * Acts as the body of the export dialog whenever the user clicks the export to drive button.
 * 
 * TODO: make this modify the version log
 * @author matt
 */
public class ExportBody extends Container {
    
    private JTextField name;
    private JComboBox<String> wayfindingType;
    private JButton next;
    
    public ExportBody() {
        super();
        setBackground(Color.red);
        setLayout(new FlowLayout());
        name = new JTextField("Enter the name for this export");
        add(name);
        
        VersionLog v = new VersionLog();
        v.download();
        String[] types = v.getTypes();
        String[] options = new String[types.length + 1];
        for(int i = 0; i < types.length; i++){
            options[i] = types[i];
        }
        options[options.length - 1] = "new type";
        wayfindingType = new JComboBox<>(options);
        add(wayfindingType);
        
        next = new JButton("Next");
        next.addActionListener((ae)->{
            if(wayfindingType.getSelectedItem().equals("new type")){
                System.out.println("new type");
            } else {
                System.out.println("not new type");
            }
        });
        add(next);
    }
    
}
