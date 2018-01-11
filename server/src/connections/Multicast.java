package connections;

import base.Configuration;
import base.RequestsHandler;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Multicast {
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
            try (
                    DatagramSocket listenerSocket = new DatagramSocket(Configuration.UDP_PORT)

            ) {
                System.out.println("[UDP] Listening on port " + Configuration.UDP_PORT);
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
                    byte[] data = packet.getData();
                    String dataString = new String(data, 0, packet.getLength(), StandardCharsets.UTF_8);
                    RequestsHandler.parseChatroomMessage(sender, dataString);
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
    
    // Force static initializer
    public static void init() { }
    
    public static void broadcast(String message, String address) {
        try {
            InetAddress group = InetAddress.getByName(address);
            byte[] data = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, group, Configuration.MULTICAST_PORT);
            socket.send(datagramPacket);
        }
        catch (IOException e) {
            System.err.println("Error while sending multicast packet");
            e.printStackTrace();
        }
    }
}
