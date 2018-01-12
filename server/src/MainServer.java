import connections.RMIManager;
import connections.Multicast;
import connections.Reception;
import connections.Tasks;
import misc.Configuration;
import misc.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * The Server Main. Starts things.
 * More precisely, it creates a thread pool and makes it manage the messages and operational connections
 */
class MainServer {

    public static void main(String[] args) {
        Utils.printDebug("DEBUG is active");

        RMIManager.start();
        Multicast.init();
        
        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);
    
        new Reception(threadPool, Configuration.PRIMARY_PORT, Tasks::primaryConnectionTask, Tasks::socketClosed);
        new Reception(threadPool, Configuration.MSG_PORT, Tasks::messageConnectionTask, Tasks::socketClosed);
    }

}
