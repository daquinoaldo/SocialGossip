package remoteinterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallbackInterface extends Remote {
    void newFriend(String username) throws RemoteException;
    void changedStatus(String username, boolean isOnline) throws RemoteException;
}
