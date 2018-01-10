package Connections;

import State.User;
import base.Configuration;
import base.Json;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Multicast {
    private Thread listener;
    private static boolean stop = false;
    private static MulticastSocket ms = null;
    private static DatagramSocket outputDatagramSocket;
    
    private static final HashMap<InetAddress, String> addressToChatname = new HashMap<>();
    
    static {
        try {
            ms = new MulticastSocket(Configuration.MULTICAST_PORT);
            outputDatagramSocket = new DatagramSocket();
        }
        catch (IOException e) {
            System.err.println("Fatal error while binding UDP port:");
            e.printStackTrace();
            System.exit(1);
        }
        
        Thread t = new Thread(() -> {
            while (!stop) {
                byte[] buffer = new byte[8192];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                
                try {
                    ms.receive(packet);
                }
                catch (IOException e) {
                    System.err.println("Error while receiving multicast packet");
                    e.printStackTrace();
                    continue;
                }
                
                // Extract data byte from packet and convert them to String
                int size = packet.getLength();
                byte[] data = packet.getData();
                String stringData = new String(data, 0, size);
                
                Json.parseChatMessage(stringData);
            }
        });
        t.start();
    }
    
    public static void send(InetAddress address, String request) {
        try {
            byte[] data = request.getBytes(StandardCharsets.UTF_8);
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address, Configuration.MULTICAST_PORT);
            outputDatagramSocket.send(datagramPacket);
        }
        catch (IOException e) {
            System.err.println("Error while sending multicast packet");
            e.printStackTrace();
        }
    }
    
    public static void joinGroup(String chatname, InetAddress address) throws IllegalArgumentException {
        if (!address.isMulticastAddress()) {
            throw new IllegalArgumentException("Not a valid multicast address: " + address.getHostName());
        }
        
        try {
            ms.joinGroup(address);
            addressToChatname.put(address, chatname);
        }
        catch (IOException e) {
            System.err.println("Error while joining group: " + address.getHostName());
            e.printStackTrace();
        }
    }
    
    public static void leaveGroup(InetAddress address) {
        try {
            ms.leaveGroup(address);
            addressToChatname.remove(address);
        }
        catch (IOException e) {
            System.err.println("Error while leaving multicacst group: " + address);
            e.printStackTrace();
        }
    }
}
