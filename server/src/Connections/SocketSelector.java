package Connections;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SocketSelector {
    private class SelectorTask implements Runnable {
        private final Consumer<Socket> realTask;
        private final Consumer<Socket> onSocketClose;
        private final ExecutorService pool;
        private final Socket socket;
        
        SelectorTask(ExecutorService pool, Socket socket, Consumer<Socket> realTask, Consumer<Socket> onSocketClose) {
            this.pool = pool;
            this.socket = socket;
            this.realTask = realTask;
            this.onSocketClose = onSocketClose;
        }
        
        public void run() {
//            if (socket.isClosed()) {
//                System.out.println("Closed connection (port " + socket.getLocalPort() + ")");
//                onSocketClose.accept(socket);
//                return;
//            }
            
            try (Reader reader = new InputStreamReader(socket.getInputStream())) {
                if (reader.ready()) {
                    realTask.accept(socket);
                }
                pool.submit(this);
            } catch (IOException e) {
                System.err.println("Error while testing socket readiness");
                e.printStackTrace();
            }
        }
    }
    
    
    private Consumer<Socket> realTask;
    private Consumer<Socket> onSocketClose;
    private ExecutorService pool;
    
    /**
     * Use a thredpool to read socket and execute a task. If the thread was able to read it, realTask will be executed.
     * In any case, the socket will be re-added to the end of the queue of the threadpool.
     *
     * If a socket gets closed it will be removed from the threadpool queue, and onSocketClosed is executed.
     *
     * @param pool, threadpool that will execute tasks.
     * @param realTask, function that accept a socket as parameter, executed when the socket is ready.
     */
    @SuppressWarnings("WeakerAccess")
    public SocketSelector(ExecutorService pool, Consumer<Socket> realTask, Consumer<Socket> onSocketClose) {
        this.pool = pool;
        this.realTask = realTask;
    }
    
    public void addSocket(Socket socket) {
        pool.submit(new SelectorTask(pool, socket, realTask, onSocketClose));
    }
}
