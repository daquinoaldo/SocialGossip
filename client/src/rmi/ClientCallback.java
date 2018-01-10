package rmi;

import State.User;
import remoteinterfaces.ClientCallbackInterface;

public class ClientCallback implements ClientCallbackInterface {
    public void newFriend(String username) {
        User.addFriend(username, true);
    }
    
    public void changedStatus(String username, boolean isOnline) {
        User.setFriendStatus(username, isOnline);
    }
}
