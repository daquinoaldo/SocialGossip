package State;

import constants.Dimensions;
import gui.Utils;
import gui.panels.ChatPanel;

import javax.swing.*;

public abstract class Chat {
    protected static final int FRIEND_TYPE = 0;
    protected static final int CHATROOM_TYPE = 1;
    
    private ChatPanel chatPanel;
    private JFrame window = null;
    
    private String name;
    private boolean flag = false; // FRIEND_TYPE -> isOnline, CHATROOM_TYPE -> isSubscribed
    
    public Chat(int type, String name) {
        if (type != FRIEND_TYPE && type != CHATROOM_TYPE)
            throw new IllegalArgumentException("Invalid type. Please choose one of FRIEND_TYPE, CHATROOM_TYPE.");
        this.name = name;
        chatPanel = new ChatPanel(name);
    }
    
    protected String getName() { return name; }
    
    protected boolean getFlag() { return flag; }
    protected void setFlag(boolean newValue) { flag = newValue; }
    
    public JFrame getWindow() { return window; }
    public void createWindow() {
        window = Utils.createWindow(name, chatPanel, Dimensions.CHAT_PANE);
    }
    
    public void newMessage(Message message) {
        if (window == null)
            createWindow();
        
        chatPanel.newMessage(message);
    }
}
