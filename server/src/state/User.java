package state;

import org.json.simple.JSONObject;
import remoteinterfaces.ClientCallbackInterface;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static misc.Utils.printDebug;
import static connections.Helpers.send;

/**
 * Server-side user modeling. Contains:
 * - the username
 * - the primary (operational) socket and the message socket and methods to manage it
 * - the RMI interface object and methods to manage it
 * - a lock that prevents 2 threads from working on the same socket
 * - the heartbeat management: every time the server receive a heartbeat from the user updates the lastHeartbeat
 *   variable, if for some time it has no heartbeats from the client it considers it disconnected
 * - the sendMsgRequest method to send a message to the user's client
 */
public class User implements Comparable<User> {
    private final String username;
    private Socket primarySocket;
    private Socket messageSocket;
    private ClientCallbackInterface callback; // RMI interface
    private final Lock lock = new ReentrantLock();
    
    // This will be used in a ScheduledThreadpoolExecutor to check if the user is still connected
    private long lastHeartbeat;
    public synchronized void setHeartbeat(long time) { this.lastHeartbeat = time; }
    public synchronized long getLastHeartbeat() { return this.lastHeartbeat; }
    private Future<?> ghostbusterFuture;
    public Future<?> getGhostbusterFuture() { return ghostbusterFuture; }
    public void setGhostbusterFuture(Future<?> ghostbusterFuture) { this.ghostbusterFuture = ghostbusterFuture; }

    public User(String username) {
        this.username = username;
    }

    // Getter
    public String getUsername() { return username; }
    public Socket getPrimarySocket() { return primarySocket; }
    public Socket getMessageSocket() { return messageSocket; }

    // Setter
    public void setPrimarySocket(Socket s) { this.primarySocket = s; }
    public void setMessageSocket(Socket s) { this.messageSocket = s; }
    public void setCallback(ClientCallbackInterface callback) {
        this.callback = callback;
    }

    public int compareTo(User u) { return this.username.compareTo(u.getUsername()); }

    /**
     * Notify the user (if is online) that another user username request a friendship
     * @param username of the user that request the friendship
     */
    public void notifyNewFriend(String username) {
        try {
            this.callback.newFriend(username);
            printDebug("[RMI] Notified " + this.username + " - " + username + " added you as a friend");
        }
        catch (RemoteException e) {
            System.err.println("Can't notify user " + this.username + " about:");
            System.err.println("-- New friend: " + username);
        }
    }

    /**
     * Notify the user that a friend change status
     * @param username of the friend that change status
     * @param isOnline the new status
     */
    public void notifyFriendStatus(String username, boolean isOnline) {
        try {
            this.callback.changedStatus(username, isOnline);
            printDebug("[RMI] Notified " + this.username + " - Friend " + username + " is gone " + (isOnline ? "online" : "offline"));
        }
        catch (RemoteException e) {
            System.err.println("Can't notify user " + this.username + " about:");
            System.err.println("-- Friend: " + username + " is gone " + (isOnline ? "online" : "offline"));
            System.err.println("Is username gone offline too?");
        }
    }

    // User lock used in Tasks to make sure that only one thread at times operates on the user socket
    public void getLock() {
        lock.lock();
    }
    
    public void releaseLock() {
        lock.unlock();
    }

    /**
     * Send a message to this user's client using the socket memorized in this object
     * @param endpoint FILE2FRIEND or MSG2FRIEND
     * @param params payload: the message
     */
    @SuppressWarnings("unchecked")
    public void sendMsgRequest(String endpoint, JSONObject params) {
        JSONObject req = new JSONObject();
        req.put("endpoint", endpoint);
        req.put("params", params);
    
        try {
            getLock();
            send(this.messageSocket, req.toJSONString());
            releaseLock();
        }
        catch (IOException e) {
            System.err.println("Error while sending message request to " + this.username);
            System.err.println("Request: " + req.toJSONString());
            e.printStackTrace();
        }
    }
}
