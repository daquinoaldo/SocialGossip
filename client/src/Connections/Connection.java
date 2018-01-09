package Connections;

import base.Configuration;
import base.Json;
import gui.Utils;

import java.io.*;
import java.net.Socket;

@SuppressWarnings("EmptyMethod")
public class Connection {
    private static final String host = Configuration.HOSTNAME;
    
    private static BufferedWriter primaryWriter;
    private static BufferedReader primaryReader;
    
    private static BufferedWriter msgWriter;
    private static BufferedReader msgReader;
    
    static {
        try {
            Socket primarySocket = new Socket(host, Configuration.PRIMARY_PORT);
            primaryWriter = new BufferedWriter(new OutputStreamWriter(primarySocket.getOutputStream()));
            primaryReader = new BufferedReader(new InputStreamReader(primarySocket.getInputStream()));
    
            Socket msgSocket = new Socket(host, Configuration.MSG_PORT);
            msgWriter = new BufferedWriter(new OutputStreamWriter(msgSocket.getOutputStream()));
            msgReader = new BufferedReader(new InputStreamReader(msgSocket.getInputStream()));
        }
        catch (IOException e) {
            System.err.println("Fatal error: can't establish connection with the server.");
            e.printStackTrace();
            System.exit(1);
        }
        
        Thread msgRequestListener = new Thread(() -> {
            while (true) {
                try {
                    String msgRequest = msgReader.readLine();
                    Json.parseMessageRequest(msgRequest);
                }
                catch (IOException e) {
                    System.err.println("Error while reading message socket");
                    e.printStackTrace();
                }
            }
        });
        msgRequestListener.start();
        
        Thread heart = new Thread(() -> {
            while (true) {
                Json.heartbeat();
                try { Thread.sleep(500); }
                catch (InterruptedException e) { }
            }
        });
        heart.start();
    }
    
    /**
     * Force static initializer to be triggered.
     */
    public static void init() {}
    
    /**
     * Send a String to the server through the primary connection. Wait for a reply and return it as a String.
     * @param request String to be sent.
     * @return String returned by the server, can be null.
     */
    public static String sendRequest(String request) {
        return send(primaryWriter, primaryReader, request);
    }
    
    /**
     * Send a String to the server through the message connection. Wait for a reply and return it as a String.
     * @param request String to be sent.
     * @return String returned by the server, can be null.
     */
    public static void sendMsgRequest(String request) {
        send(msgWriter, null, request);
    }
    
    private static String send(BufferedWriter writer, BufferedReader reader, String request) {
        try {
            writer.write(request);
            writer.newLine();
            writer.flush();
            if(Utils.isDebug) System.out.println("Connection.send: sent!");
    
            String toReturn = null;
            if (reader != null)
                toReturn = reader.readLine();
            
            if(Utils.isDebug) System.out.println("Connection.send: received!");
            return toReturn;
        }
        catch (IOException e) {
            System.err.println("Error occured while comunicating with the server.");
            e.printStackTrace();
        }
    
        return null;
    }
    
    // TODO:  Peer to peer file exchange
    public static void sendFile() {}
}
