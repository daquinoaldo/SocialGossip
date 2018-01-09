package base;

import remoteinterfaces.ClientCallbackInterface;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.concurrent.Future;

public class User implements Comparable<User> {
    private final String username;
    private Socket primarySocket;
    private Socket messageSocket;
    private ClientCallbackInterface callback; // RMI interface
    
    // This will be used in a ScheduledThreadpoolExecutor to check if the user is still connected
    private long lastHeartbeat = System.currentTimeMillis();
    public void setHeartbeat(long time) { this.lastHeartbeat = time; }
    public long getLastHeartbeat() { return this.lastHeartbeat; }
    private Future<?> ghostbusterFuture;
    public Future<?> getGhostbusterFuture() { return ghostbusterFuture; }
    public void setGhostbusterFuture(Future<?> ghostbusterFuture) { this.ghostbusterFuture = ghostbusterFuture; }
    
    
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
    
    public void sendMsgRequest(String request) {
        try {
            Connections.Helpers.send(this.messageSocket, request);
        }
        catch (IOException e) {
            System.err.println("Error while sending message request to " + this.username);
            System.err.println("Request: " + request);
            e.printStackTrace();
        }
    }
}
