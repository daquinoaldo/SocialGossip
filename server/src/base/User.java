package base;

import org.json.simple.JSONObject;
import remoteinterfaces.ClientCallbackInterface;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.Future;

import static base.Utils.printDebug;

public class User implements Comparable<User> {
    private final String username;
    private Socket primarySocket;
    private Socket messageSocket;
    private ClientCallbackInterface callback; // RMI interface
    
    // This will be used in a ScheduledThreadpoolExecutor to check if the user is still connected
    private long lastHeartbeat;
    public void setHeartbeat(long time) { this.lastHeartbeat = time; }
    public long getLastHeartbeat() { return this.lastHeartbeat; }
    private Future<?> ghostbusterFuture;
    public Future<?> getGhostbusterFuture() { return ghostbusterFuture; }
    public void setGhostbusterFuture(Future<?> ghostbusterFuture) { this.ghostbusterFuture = ghostbusterFuture; }

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
            printDebug("[RMI] Notified " + this.username + " - " + username + " added you as a friend");
        }
        catch (RemoteException e) {
            System.err.println("Can't notify user " + this.username + " about:");
            System.err.println("-- New friend: " + username);
            e.printStackTrace();
        }
    }
    
    public void notifyFriendStatus(String username, boolean isOnline) {
        try {
            this.callback.changedStatus(username, isOnline);
            printDebug("[RMI] Notified " + this.username + " - Friend " + username + " is gone " + (isOnline ? "online" : "offline"));
        }
        catch (RemoteException e) {
            System.err.println("Can't notify user " + this.username + " about:");
            System.err.println("-- Friend: " + username + " is gone " + (isOnline ? "online" : "offline"));
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void sendMsgRequest(String endpoint, JSONObject params) {
        JSONObject req = new JSONObject();
        req.put("endpoint", endpoint);
        req.put("params", params);
    
        try {
            Connections.Helpers.send(this.messageSocket, req.toJSONString());
        }
        catch (IOException e) {
            System.err.println("Error while sending message request to " + this.username);
            System.err.println("Request: " + req.toJSONString());
            e.printStackTrace();
        }
    }
}
