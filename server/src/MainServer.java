import Connections.Multicast;
import Connections.Reception;
import Connections.Tasks;
import base.Configuration;
import base.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

class MainServer {

    public static void main(String[] args) {
        Utils.printDebug("DEBUG is active");

        rmi.Manager.start();
        Multicast.init();
        
        ScheduledExecutorService threadpool = Executors.newScheduledThreadPool(4);
    
        Reception primaryConnectionsListener = new Reception(
                threadpool, Configuration.PRIMARY_PORT, Tasks::primaryConnectionTask, Tasks::socketClosed
        );
        Reception messageConnectionsListener = new Reception(
                threadpool, Configuration.MSG_PORT, Tasks::messageConnectionTask, Tasks::socketClosed
        );
    }

}
