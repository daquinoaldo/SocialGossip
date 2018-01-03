package Connections;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class Listener implements Runnable {
    public boolean mustStop = false;
    
    private Function<Socket, Runnable> taskFactory;
    private int port;
    private ExecutorService pool;
    private ServerSocket serverSocket = null;
    
    public Listener(int port, ExecutorService pool, Function<Socket, Runnable> taskFactory) {
        this.port = port;
        this.pool = pool;
        this.taskFactory = taskFactory;
    }
    
    public void run() {
        System.out.println("Server listening on port " + port);
        
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(500);
        }
        catch (IOException e) {
            System.err.println("Fatal error while binding port " + port);
            e.printStackTrace();
            System.exit(1);
        }
        
        while (!mustStop) {
            try {
                Socket socket = serverSocket.accept();
                Runnable task = taskFactory.apply(socket);
                pool.submit(task);
            }
            catch (SocketTimeoutException e) { /* socket accept timed out, not really an exception */ }
            catch (IOException e) {
                System.err.println("Error occured while accepting connection");
                e.printStackTrace();
            }
        }
    }
    
}
