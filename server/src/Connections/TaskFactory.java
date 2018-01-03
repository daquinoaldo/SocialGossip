package Connections;

import base.RequestsHandler;
import base.Utils;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;

public class TaskFactory {
    /**
     * Returns the task used when receiving a new primary connection:
     * Read a line from the socket, then parse it as a JSON executing. (See RequestsHandler for further details).
     * @param socket The socket of the incoming connection.
     * @return The task to be executed, as a Runnable.
     */
    public static Runnable primaryConnectionTask(Socket socket) {
        return () -> {
            try (
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
            ) {
                String msg = reader.readLine();
                if (Utils.isDebug)
                    System.out.println("<- Got request:\n" + msg);
                String reply = RequestsHandler.parseRequest(msg);
                if (Utils.isDebug)
                    System.out.println("-> Sending reply:\n" + reply);
                writer.write(reply);
                writer.newLine();
                writer.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
    
    /**
     * Returns the task used when receiving a new message connection.
     * @param socket The socket of the incoming connection.
     * @return The task to be executed, as a Runnable.
     */
    public static Runnable messageConnectionTask(Socket socket) {
        return () -> {
            System.out.println("Message connection not implemented yet.");
        };
    }
}
