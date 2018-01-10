package rmi;

import State.Friend;
import State.Message;
import State.User;
import remoteinterfaces.ClientCallbackInterface;

public class ClientCallback implements ClientCallbackInterface {
    public void newFriend(String username) {
        User.addFriend(username, true);
    }
    
    public void changedStatus(String username, boolean isOnline) {
        User.setFriendStatus(username, isOnline);
    
        // Show a System message if there is a chat window open
        Friend friend = User.getFriend(username);
        if (friend == null) return;
        if (friend.getWindow() == null) return;
        Message systemInfo = new Message("SYSTEM", friend.getUsername() + " is now " + (isOnline ? "online" : "offline") + ".");
        friend.newMessage(systemInfo);
    }
}
