package state;

import gui.panels.ChatPanel;

public class Friend extends Chat {

    public Friend(String username, boolean online) {
        super(Chat.TYPE_FRIEND, username);
        this.setStatus(online);
        chatPanel = new ChatPanel(this);
    }
    
    public boolean isOnline() { return getFlag(); }
    public void setStatus(boolean online) { setFlag(online); }
    
    public String getUsername() { return getName(); }
}
