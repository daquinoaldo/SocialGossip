package Connections;

import base.Configuration;
import base.RequestsHandler;

import java.io.IOException;
import java.net.*;

public class Multicast {
    private static final int port = Configuration.MULTICAST_PORT;
    private static DatagramSocket socket;
    
    static {
        try {
            socket = new DatagramSocket();
        }
        catch (IOException e) {
            System.err.println("Fatal Error: unable to create a datagram socket");
            e.printStackTrace();
            System.exit(1);
        }
        
        Thread listener = new Thread(() -> {
            try {
                DatagramSocket listenerSocket = new DatagramSocket(Configuration.MULTICAST_PORT);
    
                while (!Thread.interrupted()) {
                    byte[] buffer = new byte[8192];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
                    try {
                        listenerSocket.receive(packet);
                    } catch (IOException e) {
                        System.err.println("Error while receiving packet");
                        e.printStackTrace();
                        continue;
                    }
        
                    InetAddress sender = packet.getAddress();
                    String data = new String(packet.getData());
                    RequestsHandler.parseChatroomMessage(sender, data);
                }
            }
            catch (SocketException e) {
                System.err.println("Fatal error: can't bind UDP port");
                e.printStackTrace();
                System.exit(1);
            }
        });
        listener.start();
    }
    
    public static void broadcast(String message, String address) {
        try {
            InetAddress group = InetAddress.getByName(address);
            DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), group, port);
            socket.send(datagramPacket);
        }
        catch (IOException e) {
            System.err.println("Error while sending multicast packet");
            e.printStackTrace();
        }
    }
}
