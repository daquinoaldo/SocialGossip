import Connections.Reception;
import Connections.Tasks;
import base.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {

    public static void main(String[] args) {
        ExecutorService threadpool = Executors.newFixedThreadPool(8);
    
        Reception primaryConnectionsListener = new Reception(
                threadpool, Configuration.PRIMARY_PORT, Tasks::primaryConnectionTask, Tasks::socketClosed
        );
        Reception messageConnectionsListener = new Reception(
                threadpool, Configuration.MSG_PORT, Tasks::primaryConnectionTask, Tasks::socketClosed
        );
    }

}
