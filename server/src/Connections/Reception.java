package Connections;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

public class Reception {
    private boolean stop = false;
    private ServerSocket serverSocket = null;
    
    /**
     * Start a thread accepting connection on the specified port, create a SocketSelector which will handle the opened
     * sockets and will run task in the threadpool.
     * @param pool Threadpool that will run for the SocketSelector
     * @param port TCP port that will listen for new connections
     * @param socketTask Task to be executed when receiving messaes from a socket - see SocketSelector for details
     * @param onSocketClose Task to be executed when a socket gets closed - see SocketSelector for details
     */
    public Reception(ScheduledExecutorService pool, int port, Consumer<Socket> socketTask, Consumer<Socket> onSocketClose) {
        SocketSelector selector = new SocketSelector(pool, socketTask, onSocketClose);

        Thread listener = new Thread(() -> {
            System.out.println("Listening on port " + port);

            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.err.println("Fatal error while binding port " + port);
                e.printStackTrace();
                System.exit(1);
            }

            while (!stop) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setSoTimeout(500);
                    System.out.println("Accepted connection (port " + port + ")");
                    selector.addSocket(socket);
                } catch (SocketTimeoutException e) { /* socket accept timed out, not really an exception */ } catch (IOException e) {
                    if (stop) return; // exited normally after a stop request
                    System.err.println("Error occured while accepting connection");
                    e.printStackTrace();
                }
            }
        });
        listener.start();
    }
    
    /**
     * Stop accepting connection. The effect is not immediate.
     */
    public void stop() {
        stop = true;
        try {
            serverSocket.close();
        }
        catch (IOException e) {
            System.err.println("Error while closing ServerSocket");
            e.printStackTrace();
        }
    }
}
