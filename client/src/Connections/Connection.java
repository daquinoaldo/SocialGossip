package Connections;

import base.Configuration;
import base.Filesystem;
import base.Json;
import base.State;
import gui.Utils;

import java.io.*;
import java.net.*;
import java.nio.channels.SocketChannel;

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
            while (!Thread.interrupted()) {
                Json.heartbeat();
                try { Thread.sleep(500); }
                catch (InterruptedException e) { }
            }
        });
        State.addLoginListener((loggedIn) -> {
            if (loggedIn) heart.start();
            else heart.interrupt();
        });
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
            
            String toReturn = null;
            if (reader != null)
                toReturn = reader.readLine();
            
            return toReturn;
        }
        catch (IOException e) {
            System.err.println("Fatal error occured while comunicating with the server.");
            e.printStackTrace();
            System.exit(1);
        }
    
        return null;
    }
    
    // TODO:  Peer to peer file exchange
    public static void receiveFile(File destFile, String hostname, int port) {
        Thread asyncWriter = new Thread(() -> {
            int failedCount = 0;
            boolean stop = false;
            do {
                try {
                    Socket socket = new Socket(hostname, port);
                    socket.setSoTimeout(5000);
                    Filesystem.writeFile(socket, destFile);
                } catch (IOException e) {
                    if (failedCount < 3) {
                        try {
                            failedCount++;
                            Thread.sleep(5000);
                        } catch (InterruptedException intexc) { }
                    }
                    else {
                        stop = true;
                        System.err.println("Can't connect to the other peer");
                        e.printStackTrace();
                        
                    }
                }
            } while (!stop);
        });
        asyncWriter.start();
    }
    
    public static ServerSocket openFileSocket() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            serverSocket.setSoTimeout(5000);
            return serverSocket;
        }
        catch (IOException e) {
            System.err.println("Error while openening p2p socket for sending file");
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    public static void startFileSender(ServerSocket serverSocket, File file) {
        Thread listener = new Thread(() -> {
            try {
                serverSocket.setSoTimeout(5000);
                Socket socket = serverSocket.accept();
                socket.setSoTimeout(5000);
                
                Filesystem.readFile(file, socket);
            
                Utils.showErrorDialog("The user refused to receive the file.");
            } catch (IOException e) {
                System.err.println("Error while accepting connection from other peer");
                e.printStackTrace();
            }
        });
        listener.start();
    }
}
