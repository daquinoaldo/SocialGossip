package connections;

import misc.Configuration;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * UDP connections class.
 * Contains methods to send the UDP multicast messages (the rooms broadcast)
 */
public class Multicast {
    @SuppressWarnings("CanBeFinal")
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

    /**
     * Force static initializer
     */
    @SuppressWarnings("EmptyMethod")
    public static void init() { }

    /**
     * Message broadcast on the multicast socket
     * @param message to be sent
     * @param address of the multicast
     */
    public static void broadcast(String message, String address) {
        try {
            if(socket == null) throw new IOException("Socket is null");
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
