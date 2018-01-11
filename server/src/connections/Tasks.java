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
            String req = recv(socket);
            
            User user = OnlineUsers.getBySocket(socket);
            if (user == null) {
                user = new User("stub");
                user.setPrimarySocket(socket);
            }
    
            String reply = RequestsHandler.parseRequest(user, req);
    
            send(socket, reply);
        }
        catch (IOException e) {
            System.err.println("Error while reading request (primary connection):");
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the task used when receiving a new message connection.
     * @param socket The socket of the incoming connection.
     */
    public static void messageConnectionTask(Socket socket) {
        try {
            String req = recv(socket);
            
            User user = OnlineUsers.getBySocket(socket);
            if (user == null) {
                user = new User("stub");
                user.setMessageSocket(socket);
            }
            
            String reply = RequestsHandler.parseMessageRequest(user, req);
            
            send(socket, reply);
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
