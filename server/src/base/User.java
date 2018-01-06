package base;

import remoteinterfaces.ClientCallbackInterface;

import java.net.Socket;

public class User implements Comparable<User> {
    private String username;
    private Socket primarySocket;
    private Socket messageSocket;
    public ClientCallbackInterface notify; // RMI interface
    
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
}
