package state;

import gui.constants.Dimensions;
import gui.Utils;
import gui.panels.ChatPanel;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class Chat {
    static final int TYPE_FRIEND = 0;
    static final int TYPE_ROOM = 1;
    
    @SuppressWarnings("WeakerAccess")
    protected ChatPanel chatPanel;
    private JFrame window = null;

    private int type;
    private String name;
    private boolean flag = false; // TYPE_FRIEND -> isOnline, TYPE_ROOM -> isSubscribed
    
    Chat(int type, String name) {
        if (type != TYPE_FRIEND && type != TYPE_ROOM)
            throw new IllegalArgumentException("Invalid type. Please choose one of TYPE_FRIEND, TYPE_ROOM.");
        this.type = type;
        this.name = name;
    }

    public String getName() { return name; }
    
    @SuppressWarnings("WeakerAccess")
    protected boolean getFlag() { return flag; }
    @SuppressWarnings("WeakerAccess")
    protected void setFlag(boolean newValue) { flag = newValue; }
    
    public JFrame getWindow() { return window; }
    public void createWindow() {
        if (window == null) {
            String windowsName = name;
            if (type == TYPE_ROOM) windowsName += " - "+User.username();
            window = Utils.createWindow(windowsName, chatPanel, Dimensions.CHAT_PANE);

            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened( WindowEvent e){
                    chatPanel.focusGained();
                }
    
                @Override
                public void windowClosing(WindowEvent e) {
                    window = null;
                    e.getWindow().dispose();
                }
                
                @Override
                public void windowActivated(WindowEvent e) {
                    chatPanel.focusGained();
                }
            });
        }
        else
            window.requestFocus();
    }
    public void closeWindow() {
        if (window == null)
            return;
        window.dispose();
    }
    
    public void newMessage(Message message) {
        if (window == null)
            createWindow();
        
        chatPanel.newMessage(message);
    }
}
