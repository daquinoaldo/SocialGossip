package State;

public class Room extends Chat {
    public Room(String username) {
        super(Chat.CHATROOM_TYPE, username);
    }
    
    public Room(String username, boolean online) {
        super(Chat.CHATROOM_TYPE, username);
        this.setStatus(online);
    }
    
    public boolean isSubscribed() { return getFlag(); }
    public void setStatus(boolean subscribed) { setFlag(subscribed); }
    
    public String getName() { return getName(); }
}
