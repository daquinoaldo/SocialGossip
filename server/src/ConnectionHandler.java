import base.Configuration;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionHandler implements Runnable {
    public boolean mustStop = false;
    
    private int port = Configuration.PRIMARY_PORT;
    private ExecutorService pool = Executors.newFixedThreadPool(4);
    private ServerSocket serverSocket = null;
    
    public void run() {
        System.out.println("Server listening for connections on port " + port);
        
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(500);
        }
        catch (IOException e) {
            System.err.println("Fatal error while binding port.");
            e.printStackTrace();
            System.exit(1);
        }
        
        while (!mustStop) {
            try {
                Socket socket = serverSocket.accept();
                pool.submit(() -> {
                    try (
                            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
                    ) {
                        String msg = input.readLine();
                        if (Utils.isDebug) System.out.println("<- Got request:\n" + msg);
                        JSONObject reply = RequestsHandler.parseRequest(msg);
                        if (Utils.isDebug) System.out.println("-> Sending reply:\n" + reply.toJSONString());
                        output.newLine();
                        output.flush();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            catch (SocketTimeoutException e) { /* socket accept timed out, not really an exception */ }
            catch (IOException e) {
                System.err.println("Error occured while accepting connection");
                e.printStackTrace();
            }
        }
    }
}
