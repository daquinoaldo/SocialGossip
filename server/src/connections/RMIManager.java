package connections;

import misc.Configuration;
import state.OnlineUsers;
import state.User;
import remoteinterfaces.ClientCallbackInterface;
import remoteinterfaces.ServerInterface;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMI RMIManager: register the RMI callback specified in ServerInterface used by the Client to record its callbacks
 */
public class RMIManager {

    /**
     * Inner class, implementation of the ServerInterface
     */
    public static class ServerImplementation implements ServerInterface {
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

    @SuppressWarnings("CanBeFinal")
    private static Registry registry;
    
    static {
        try {
            LocateRegistry.createRegistry(Configuration.RMI_PORT);
            registry = LocateRegistry.getRegistry(Configuration.RMI_PORT);
        }
        catch (RemoteException e) {
            System.err.println("Fatal error: can't start RMI registry");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void start() {
        try {
            ServerImplementation impl = new ServerImplementation();
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(impl, 0);
            registry.bind(Configuration.RMI_NAME, stub);
            System.out.println("[RMI] Listening on port " + Configuration.RMI_PORT);
        }
        catch (RemoteException e) {
            System.err.println("Fatal error: can't bind remote object");
            e.printStackTrace();
            System.exit(1);
        }
        catch (AlreadyBoundException e) {
            System.err.println("Fatal error: server remote object was already exported");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
