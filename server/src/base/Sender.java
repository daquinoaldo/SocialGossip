package base;

import java.io.IOException;
import java.net.*;

public class Sender {

    public void sendBroadcast(String message, String address) throws IOException {
        int port = 6789;    //TODO: quale porta??
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(address);
        DatagramPacket datagramPacket = new DatagramPacket(message.getBytes(), message.length(), group, port);
        socket.send(datagramPacket);
        socket.close();
    }
}
