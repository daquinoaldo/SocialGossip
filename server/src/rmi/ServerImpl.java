package rmi;

import base.OnlineUsers;
import base.User;
import remoteinterfaces.ClientCallbackInterface;
import remoteinterfaces.ServerInterface;

public class ServerImpl implements ServerInterface {
    public boolean registerCallback(String username, ClientCallbackInterface callback) {
        User user = OnlineUsers.getByUsername(username);
        if (user == null) {
            // user not found
            System.err.println("Tried to register callback for an offline user: " + username);
            return false;
        }
        else {
            user.setCallback(callback);
            return true;
        }
    }
}
