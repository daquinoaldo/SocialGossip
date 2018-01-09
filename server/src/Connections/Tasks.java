package Connections;

import base.OnlineUsers;
import base.RequestsHandler;
import base.User;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import static base.Utils.printDebug;

public class Tasks {
    /**
     * Returns the task used when receiving a new primary connection:
     * Read a line from the socket, then parse it as a JSON executing. (See RequestsHandler for further details).
     * @param socket The socket of the incoming connection.
     */
    public static void primaryConnectionTask(Socket socket) {
        try {
            // Note: closing the reader or the writer will close the original socket too.
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    
            String req = reader.readLine();
            
            printDebug("<- [PRIMARY] Got request:\n" + req);
            
            User user = OnlineUsers.getBySocket(socket);
            if (user == null) {
                user = new User("stub");
                user.setPrimarySocket(socket);
            }
    
            String reply = RequestsHandler.parseRequest(user, req);
    
            printDebug("-> [PRIMARY] Sending reply:\n" + reply);
            
            writer.write(reply);
            writer.newLine();
            writer.flush();
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
            // Note: closing the reader or the writer will close the original socket too.
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    
            String req = reader.readLine();
        
            printDebug("<- [MESSAGE] Got request:\n" + req);
            
            User user = OnlineUsers.getBySocket(socket);
            if (user == null) {
                user = new User("stub");
                user.setMessageSocket(socket);
            }
            
            String reply = RequestsHandler.parseRequest(user, req);

            printDebug("-> [MESSAGE] Sending reply:\n" + reply);

            writer.write(reply);
            writer.newLine();
            writer.flush();
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
        if(!OnlineUsers.remove(user)) System.err.println("Error while changing user status\n" +
                "Error occurred in Task.socketClosed with user "+user.toString());
    
        for (User friend : friends) {
            friend.notifyFriendStatus(user.getUsername(), false);
        }
    }
}
