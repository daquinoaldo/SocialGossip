package connections;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Connection Helpers: sends and receives things over the sockets
 */
public class Helpers {
    public static void send(Socket socket, String s) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(s);
        writer.newLine();
        writer.flush();
    }
    
    static ArrayList<String> receive(Socket socket) throws IOException {
        // Note: closing the reader or the writer will close the original socket too.
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
        ArrayList<String> lines = new ArrayList<>();
        try {
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
        }
        catch (SocketTimeoutException e) { /* socket timed out -> no new lines */}
        
        return lines;
    }
}
