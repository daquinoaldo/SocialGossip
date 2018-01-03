package base;

import java.io.*;
import java.net.Socket;

public class Connection {
    private static final String host = Configuration.HOSTNAME;
    private static final int port = Configuration.PRIMARY_PORT;
    private static final int msgPort = Configuration.MSG_PORT;
    
    private static Socket msgSocket;
    private static BufferedWriter msgWriter;
    private static BufferedReader msgReader;
    
    static {
        try {
            msgSocket = new Socket(host, msgPort);
            msgWriter = new BufferedWriter(new OutputStreamWriter(msgSocket.getOutputStream()));
            msgReader = new BufferedReader(new InputStreamReader(msgSocket.getInputStream()));
        }
        catch (IOException e) {
            System.err.println("Fatal error: can't establish message connection with the server.");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Send a String to the server through the primary connection. Wait for a reply and return it as a String.
     * @param request String to be sent.
     * @return String returned by the server, can be null.
     */
    public static String sendRequest(String request) {
        try (
                Socket socket = new Socket(host, port);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            writer.write(request);
            writer.newLine();
            writer.flush();
    
            return reader.readLine();
        }
        catch (IOException e) {
            System.err.println("Error occured while comunicating with the server.");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Send a String to the server through the message connection. Wait for a reply and return it as a String.
     * @param request String to be sent.
     * @return String returned by the server, can be null.
     */
    public static String sendMsgRequest(String request) {
        try {
            msgWriter.write(request);
            msgWriter.newLine();
            msgWriter.flush();
            
            return msgReader.readLine();
        }
        catch (IOException e) {
            System.err.println("Error occured while comunicating with the server.");
            e.printStackTrace();
        }
        
        return null;
    }
}
