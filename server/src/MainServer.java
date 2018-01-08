import Connections.Reception;
import Connections.Tasks;
import base.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class MainServer {

    public static void main(String[] args) {
        rmi.Manager.start();
        ExecutorService threadpool = Executors.newFixedThreadPool(8);
    
        Reception primaryConnectionsListener = new Reception(
                threadpool, Configuration.PRIMARY_PORT, Tasks::primaryConnectionTask, Tasks::socketClosed
        );
        Reception messageConnectionsListener = new Reception(
                threadpool, Configuration.MSG_PORT, Tasks::messageConnectionTask, Tasks::socketClosed
        );
    }

}
