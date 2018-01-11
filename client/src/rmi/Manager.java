package rmi;

import state.User;
import base.Configuration;
import remoteinterfaces.ClientCallbackInterface;
import remoteinterfaces.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Manager {
    private static final ClientCallbackInterface callback = new ClientCallback();
    @SuppressWarnings("CanBeFinal")
    private static ServerInterface server;
    
    static {
        try {
            Registry registry = LocateRegistry.getRegistry(Configuration.HOSTNAME, Configuration.RMI_PORT);
            server = (ServerInterface) registry.lookup(Configuration.RMI_NAME);
            
        }
        catch (RemoteException e) {
            System.err.println("Fatal error: cannot connect to RMI registry.");
            e.printStackTrace();
            System.exit(1);
        }
        catch (NotBoundException e) {
            System.err.println("Fatal error: cannot find " + Configuration.RMI_NAME + " in RMI registry.");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void registerCallback() {
        try {
            ClientCallbackInterface stub = (ClientCallbackInterface) UnicastRemoteObject.exportObject(callback, 0);
            boolean success = server.registerCallback(User.username(), stub);
            if (!success) {
                throw new RemoteException("User offline for the server");
            }
        }
        catch (RemoteException e) {
            System.err.println("Fatal error: can't register callback in RMI server");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
