package nodemanager.gui;

import java.awt.BorderLayout;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/**
 * This will eventually be a GUI component to replace JOptionPane
 * @author Matt
 */
public class InputConsole extends JPanel {
    private final JTextArea text;
    private final JScrollBar bar;
    private final StringBuilder msgs;
    private static InputConsole instance;
    
    private InputConsole(){
        super();
        setLayout(new BorderLayout());
        msgs = new StringBuilder();
        text = new JTextArea();
        text.setEditable(false);
        text.setWrapStyleWord(true);
        text.setLineWrap(true);
        JScrollPane scroll = new JScrollPane(text);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        bar = scroll.getVerticalScrollBar();
        add(scroll, BorderLayout.CENTER);
        
        writeMessage("=== Messages will appear here ===");
    }
    
    public static InputConsole getInstance(){
        if(instance == null){
            instance = new InputConsole();
        }
        return instance;
    }
    
    public final void writeMessage(String message){
        msgs.append(message).append("\n");
        text.setText(msgs.toString());
        SwingUtilities.invokeLater(()->{
            bar.setValue(bar.getMaximum());
        });
    }
    
    public final void warn(String message){
        JOptionPane.showMessageDialog(this, message);
    }
    
    public final void askString(String message, Consumer<String> then){
        String response = JOptionPane.showInputDialog(this, message);
        then.accept(response);
    }
    
    // need to change to non-blocking once I implement into GUI
    public final void askInt(String message, Consumer<Integer> then){
        String response = JOptionPane.showInputDialog(this, message);
        try {
            int asInt = Integer.parseInt(response);
            then.accept(asInt);
        } catch(NumberFormatException ex){
            warn(String.format("I couldn't find a number in %s", response));
        }
    }
}
