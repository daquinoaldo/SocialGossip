package connections;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

class MainMulticastTest2 {

    public static void main (String [ ] args) {
        try {
            MulticastSocket ms = new MulticastSocket(8787);
            InetAddress ia = InetAddress.getByName("239.8.185.226");
            ms.joinGroup (ia);
            System.out.println("Joinato");
        } catch (IOException e) {
            System.err.println("Errore");
            e.printStackTrace();
        }
    }
}

