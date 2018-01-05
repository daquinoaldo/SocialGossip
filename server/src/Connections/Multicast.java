package Connections;

import base.Configuration;

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
