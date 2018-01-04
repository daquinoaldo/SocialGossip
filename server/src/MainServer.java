import Connections.Listener;
import Connections.TaskFactory;
import base.Configuration;
import base.Database;
import base.Utils;
import exceptions.*;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class MainServer {

    public static void main(String[] args) {
        ExecutorService threadpool = Executors.newFixedThreadPool(8);

        Thread primaryListener = startListener(Configuration.PRIMARY_PORT, threadpool, TaskFactory::primaryConnectionTask);
        Thread messageListener = startListener(Configuration.MSG_PORT, threadpool, TaskFactory::messageConnectionTask);
    }

    private static Thread startListener(int port, ExecutorService pool, Function<Socket, Runnable> taskFactory) {
        Listener listener = new Listener(port, pool, taskFactory);
        Thread t = new Thread(listener);
        t.start();
        return t;
    }
}
