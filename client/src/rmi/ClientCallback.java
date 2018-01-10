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
        Friend friend = User.getFriend(username);
        friend.setStatus(isOnline);
        friend.newMessage(new Message("SYSTEM", friend.getUsername() + " is now " + (isOnline ? "online" : "offline") + "."));
    }
}
