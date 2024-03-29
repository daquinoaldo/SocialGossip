package connections;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A sort of selector for non-rmi sockets
 * @see this constructor for more details
 */
class SocketSelector {

    /**
     * Inner class: Task to check if a socket is ready to be read
     */
    private class SelectorTask implements Runnable {
        private final Consumer<Socket> realTask;
        private final Consumer<Socket> onSocketClose;
        private final ScheduledExecutorService pool;
        private final Socket socket;
        private InputStreamReader reader = null;
        
        SelectorTask(ScheduledExecutorService pool, Socket socket, Consumer<Socket> realTask, Consumer<Socket> onSocketClose) {
            this.pool = pool;
            this.socket = socket;
            
            try {
                socket.setSoTimeout(100);
                this.reader = new InputStreamReader(socket.getInputStream());
            }
            catch (IOException e) {
                System.err.println("Error while setting socket.");
                e.printStackTrace();
            }
            this.realTask = realTask;
            this.onSocketClose = onSocketClose;
        }
        
        public void run() {
            if (socket.isClosed()) {
                System.out.println("Closed connection (port " + socket.getLocalPort() + ")");
                onSocketClose.accept(socket);
                return;
            }
            
            try {
                if (reader.ready()) {
                    realTask.accept(socket);
                }
                pool.schedule(this, 500, TimeUnit.MILLISECONDS);
            } catch (IOException e) {
                System.err.println("Error while testing socket readiness");
                e.printStackTrace();
            }
        }
    }
    
    
    private final Consumer<Socket> realTask;
    private final Consumer<Socket> onSocketClose;
    private final ScheduledExecutorService pool;
    
    /**
     * Use a thread pool to read socket and execute a task. If the thread was able to read it, realTask will be executed.
     * In any case, the socket will be re-added to the end of the queue of the thread pool.
     * If a socket gets closed it will be removed from the thread pool queue, and onSocketClosed is executed.
     * @param pool, thread pool that will execute tasks.
     * @param realTask, function that accept a socket as parameter, executed when the socket is ready.
     */
    SocketSelector(ScheduledExecutorService pool, Consumer<Socket> realTask, Consumer<Socket> onSocketClose) {
        this.pool = pool;
        this.realTask = realTask;
        this.onSocketClose = onSocketClose;
    }
    
    public void addSocket(Socket socket) {
        pool.submit(new SelectorTask(pool, socket, realTask, onSocketClose));
    }
}
