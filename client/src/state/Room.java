package state;

import connections.Multicast;
import gui.panels.ChatPanel;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Room extends Chat {
    private InetAddress address;
    private String creator;
    
    public Room(String username, String mcAddress, String creator, boolean subscribed) throws UnknownHostException {
        super(Chat.TYPE_ROOM, username);
        if (mcAddress == null || creator == null)
            throw new IllegalArgumentException("Invalid chat parameters: <" + mcAddress + "," + creator + ">");
        
        this.setStatus(subscribed);
        this.address = InetAddress.getByName(mcAddress);
        this.creator = creator;
        Multicast.joinGroup(username, address);
        chatPanel = new ChatPanel(this);
    }
    
    public void leaveMulticastGroup() { Multicast.leaveGroup(this.address); }
    public String getCreator() { return creator; }
    public boolean isSubscribed() { return getFlag(); }
    public void setStatus(boolean subscribed) { setFlag(subscribed); }
}
