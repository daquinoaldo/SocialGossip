package State;

public class Friend extends Chat {
    public Friend(String username) {
        super(Chat.FRIEND_TYPE, username);
    }
    
    public Friend(String username, boolean online) {
        super(Chat.FRIEND_TYPE, username);
        this.setStatus(online);
    }
    
    public boolean isOnline() { return getFlag(); }
    public void setStatus(boolean online) { setFlag(online); }
    
    public String getUsername() { return getName(); }
}
