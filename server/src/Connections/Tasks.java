package Connections;

import base.OnlineUsers;
import base.RequestsHandler;
import base.User;
import base.Utils;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Tasks {
    /**
     * Returns the task used when receiving a new primary connection:
     * Read a line from the socket, then parse it as a JSON executing. (See RequestsHandler for further details).
     * @param socket The socket of the incoming connection.
     * @return The task to be executed, as a Runnable.
     */
    public static void primaryConnectionTask(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String msg = reader.readLine();
            
            if (Utils.isDebug)
                System.out.println("<- [PRIMARY] Got request:\n" + msg);
            
            User user = OnlineUsers.getBySocket(socket);
            String reply = RequestsHandler.parseRequest(user, msg);
            
            if (Utils.isDebug)
                System.out.println("-> [PRIMARY] Sending reply:\n" + reply);
            
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
     * @return The task to be executed, as a Runnable.
     */
    public static void messageConnectionTask(Socket socket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String msg = reader.readLine();
        
            if (Utils.isDebug)
                System.out.println("<- [MESSAGE] Got request:\n" + msg);
            // TODO
            //            User user = OnlineUsers.getBySocket(socket);
            //            String reply = RequestsHandler.parseMessageRequest(user, msg);
            //
            //            if (Utils.isDebug)
            //                System.out.println("-> [MESSAGE] Sending reply:\n" + reply);
            //
            //            writer.write(reply);
            //            writer.newLine();
            //            writer.flush();
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
        OnlineUsers.remove(user);
    
        for (User friend : friends) {
            friend.notify.changedStatus(user.getUsername(), false);
        }
    }
}
