package remoteinterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    boolean registerCallback(String username, ClientCallbackInterface callback) throws RemoteException;
}
