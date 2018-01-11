package connections;

import base.Configuration;
import base.Json;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static javax.management.Query.TIMES;

@SuppressWarnings("ALL")
public class MulticastNIO {
    @SuppressWarnings("CanBeFinal")
    private static DatagramSocket outputDatagramSocket;
    private static DatagramChannel datagramChannel;
    private static NetworkInterface networkInterface;
    
    private static final HashMap<InetAddress, String> addressToChatname = new HashMap<>();
    
    static {
        try {
            networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName("localhost"));
            datagramChannel = DatagramChannel.open(StandardProtocolFamily.INET);    // force IPv4
            datagramChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            datagramChannel.bind(new InetSocketAddress(Configuration.MULTICAST_PORT));
            datagramChannel.setOption(StandardSocketOptions.IP_MULTICAST_IF, networkInterface);
            datagramChannel.configureBlocking(true);

            outputDatagramSocket = new DatagramSocket();
        }
        catch (IOException e) {
            System.err.println("Fatal error while binding UDP port:");
            e.printStackTrace();
            System.exit(1);
        }

        Thread listener = new Thread(() -> {
            while (!Thread.interrupted()) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(8192);
                String stringData = "";
                try {
                    for (int i = 0; i < TIMES; i++) {
                        byteBuffer.clear();
                        datagramChannel.receive(byteBuffer);
                        byteBuffer.flip();
                        stringData = new String(byteBuffer.array()).trim();  // trim() deletes white spaces in the string
                    }
                } catch (IOException e) {
                    System.err.println("Error while receiving multicast packet");
                    e.printStackTrace();
                    continue;
                }

                Json.parseChatMessage(stringData);
            }
        });
        listener.start();
    }
    
    public static void send(String request) {
        try {
            InetAddress destAddress = InetAddress.getByName(Configuration.HOSTNAME);
            byte[] data = request.getBytes(StandardCharsets.UTF_8);
            DatagramPacket datagramPacket = new DatagramPacket(data, data.length, destAddress, Configuration.UDP_PORT);
            outputDatagramSocket.send(datagramPacket);
        }
        catch (IOException e) {
            System.err.println("Error while sending UDP packet");
            e.printStackTrace();
        }
    }
    
    public static void joinGroup(String chatname, InetAddress address) throws IllegalArgumentException {
        if (!address.isMulticastAddress()) {
            throw new IllegalArgumentException("Not a valid multicast address: " + address.getHostName());
        }
        
        if (addressToChatname.containsValue(chatname))
            return;
        
        try {
            datagramChannel.join(address, networkInterface);
            addressToChatname.put(address, chatname);
        }
        catch (IOException e) {
            System.err.println("Error while joining group: " + address.getHostName());
            e.printStackTrace();
        }
    }
    
    public static void leaveGroup(InetAddress address) {
        try {
            //datagramChannel.leave(group) non funziona
            if(false) throw new IOException(".");
            addressToChatname.remove(address);
        }
        catch (IOException e) {
            System.err.println("Error while leaving multicast group: " + address.getHostName());
            e.printStackTrace();
        }
    }
}
