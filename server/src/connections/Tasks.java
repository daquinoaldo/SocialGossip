package connections;

import base.OnlineUsers;
import base.RequestsHandler;
import base.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static connections.Helpers.recv;
import static connections.Helpers.send;

public class Tasks {
    /**
     * Returns the task used when receiving a new primary connection:
     * Read a line from the socket, then parse it as a JSON executing. (See RequestsHandler for further details).
     * @param socket The socket of the incoming connection.
     */
    public static void primaryConnectionTask(Socket socket) {
        try {
            User user = OnlineUsers.getBySocket(socket);
            if (user == null) {
                user = new User("stub");
                user.setPrimarySocket(socket);
            }
    
            user.getLock();
            ArrayList<String> requests = recv(socket);
            user.releaseLock();
    
            for (String req : requests) {
                String reply = RequestsHandler.parseRequest(user, req);
    
                user.getLock();
                send(socket, reply);
                user.releaseLock();
            }
        }
        catch (IOException e) {
            System.err.println("Error while reading request (primary connection):");
            e.printStackTrace();
        }
    }
    
    /**
     * Task used when receiving a new message connection.
     * Send the read string to the JSON layer, and update the user heartbeat information.
     * @param socket The socket of the incoming connection.
     */
    public static void messageConnectionTask(Socket socket) {
        try {
            User user = OnlineUsers.getBySocket(socket);
            if (user == null) {
                user = new User("stub");
                user.setMessageSocket(socket);
            }
    
            user.getLock();
            ArrayList<String> requests = recv(socket);
            user.setHeartbeat( System.currentTimeMillis() );
            user.releaseLock();
    
            for (String req : requests) {
                String reply = RequestsHandler.parseMessageRequest(user, req);
    
                if (reply != null) {
                    user.getLock();
                    send(socket, reply);
                    user.releaseLock();
                }
            }
        }
        catch (IOException e) {
            System.err.println("Error while reading request (primary connection):");
            e.printStackTrace();
        }
    }
    
    public static void socketClosed(Socket socket) {
        User user = OnlineUsers.getBySocket(socket);
        if (user == null) return;
        
        ArrayList<User> friends = OnlineUsers.getOnlineFriends(user);
        
        if(!OnlineUsers.remove(user)) System.err.println("User was already offline");
        
        for (User friend : friends) {
            friend.notifyFriendStatus(user.getUsername(), false);
        }
    }
}
