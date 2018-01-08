package base;

import remoteinterfaces.ClientCallbackInterface;

import java.net.Socket;
import java.rmi.RemoteException;

public class User implements Comparable<User> {
    private final String username;
    private Socket primarySocket;
    private Socket messageSocket;
    private ClientCallbackInterface callback; // RMI interface
    
    @SuppressWarnings("WeakerAccess")
    public User(String username) {
        this.username = username;
    }
    
    public String getUsername() { return username; }
    public Socket getPrimarySocket() { return primarySocket; }
    public Socket getMessageSocket() { return messageSocket; }
    
    public void setPrimarySocket(Socket s) { this.primarySocket = s; }
    public void setMessageSocket(Socket s) { this.messageSocket = s; }
    
    public int compareTo(User u) { return this.username.compareTo(u.getUsername()); }
    
    public void setCallback(ClientCallbackInterface callback) {
        this.callback = callback;
    }
    
    public void notifyNewFriend(String username) {
        try {
            this.callback.newFriend(username);
        }
        catch (RemoteException e) {
            System.err.println("Can't notify user " + this.username + " about:");
            System.err.println("-- New friend: " + username);
            // TODO: rimuovere questo utente dagli utenti online
        }
    }
    
    public void notifyFriendStatus(String username, boolean isOnline) {
        try {
            this.callback.changedStatus(username, isOnline);
        }
        catch (RemoteException e) {
            System.err.println("Can't notify user " + this.username + " about:");
            System.err.println("-- Friend: " + username + "is gone " + (isOnline ? "online" : "offline"));
        }
    }
}
