package rmi;

import base.Configuration;
import remoteinterfaces.ServerInterface;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Manager {
    static ServerImpl impl;
    static Registry registry;
    
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
            impl = new ServerImpl();
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
