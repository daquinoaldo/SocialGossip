package Connections;

import java.io.*;
import java.net.Socket;

public class Helpers {
    public static void send(Socket socket, String s) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write(s);
        writer.newLine();
        writer.flush();
    }
    
    public static String recv(Socket socket) throws IOException {
        // Note: closing the reader or the writer will close the original socket too.
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return reader.readLine();
    }
}
