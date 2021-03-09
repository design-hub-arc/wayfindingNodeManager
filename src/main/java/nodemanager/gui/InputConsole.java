package nodemanager.gui;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.function.Consumer;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/**
 * A GUI component used to write messages to the user, and receive input from
 * them.
 * 
 * @author Matt Crow
 */
public class InputConsole extends JPanel {
    private final JTextArea text;
    private final JTextField inputField;
    private final JScrollBar bar;
    private final StringBuilder msgs;
    private final LinkedList<Consumer<String>> waitingCommands;
    
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
        
        inputField = new JTextField();
        add(inputField, BorderLayout.PAGE_END);
        inputField.addActionListener((e)->{
            readInput();
        });
        
        waitingCommands = new LinkedList<>();
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
        writeMessage(String.format("!Warning: %s", message));
    }
    
    /**
     * Run whenever the user enters something into the text field.
     * If their are any waiting commands, dequeues the first one,
     * and feeds it the user's input
     */
    private void readInput(){
        if(waitingCommands.isEmpty()){
            writeMessage("No commands waiting");
        } else {
            waitingCommands.poll().accept(inputField.getText());
        }
        writeMessage(String.format("<= %s", inputField.getText()));
        inputField.setText("");
    }
    
    public final void askString(String message, Consumer<String> then){
        writeMessage(message);
        waitingCommands.addLast(then);
    }
    
    public final void askInt(String message, Consumer<Integer> then){
        writeMessage(message);
        waitingCommands.addLast((str)->{
            try {
                int asInt = Integer.parseInt(str);
                then.accept(asInt);
            } catch(NumberFormatException ex){
                warn(String.format("I couldn't find a number in %s", str));
            }
        });
    }
}
