package rmi;

import base.State;
import remoteinterfaces.ClientCallbackInterface;

public class ClientCallback implements ClientCallbackInterface {
    public void newFriend(String username) {
        State.addFriend(username);
    }
    
    public void changedStatus(String username, boolean isOnline) {
        State.setFriendStatus(username, isOnline);
    }
}
