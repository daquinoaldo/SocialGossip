package base;

import Connections.Tasks;

import static base.Utils.printDebug;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

public class OnlineUsers {
    private static final long FIVE_SECONDS = 5000;
    private static final Database db = new Database();
    private static final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    
    private static final ScheduledExecutorService ghostbuster = Executors.newScheduledThreadPool(1);
    
    public static boolean add(User user) {
        boolean result = users.putIfAbsent(user.getUsername(), user) == null;
        
        if (result) {
            // Add the user to the ghostbusting service
            ScheduledFuture<?> future = ghostbuster.scheduleAtFixedRate(() -> {
                long lastHeartbeat = user.getLastHeartbeat();
                long now = System.currentTimeMillis();
                
                if (lastHeartbeat != 0 && now - lastHeartbeat > FIVE_SECONDS) {
                    // If last heartbeat was 5 or more seconds ago
                    printDebug("[GHOSTBUSTER] Ghost found! Username: " + user.getUsername());
    
                    user.getGhostbusterFuture().cancel(true);
                    try {
                        user.getPrimarySocket().close();
                        user.getMessageSocket().close();
                    } catch (IOException e) {
                        System.err.println("Error closing sockets for user: " + user.getUsername());
                        e.printStackTrace();
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
            user.setGhostbusterFuture(future);
        }
        
        return result;
    }
    
    public static boolean remove(User user) {
        return users.remove(user.getUsername()) != null;
    }
    
    public static boolean isOnline(String username) {
        return users.containsKey(username);
    }
    
    public static boolean isOnline(User u) { return isOnline(u.getUsername()); }
    
    public static User getByUsername(String username) {
        return users.get(username);
    }
    
    public static User getBySocket(Socket s) {
        for (User user : users.values())
            if (user.getPrimarySocket() == s || user.getMessageSocket() == s) return user;
        
        return null;
    }
    
    public static ArrayList<User> getOnlineFriends(User u) {
        List<String> friendsUsernames = db.getFriendships(u.getUsername());
        ArrayList<User> result = new ArrayList<>();
        for (String username : friendsUsernames)
            if (users.containsKey(username))
                result.add(users.get(username));
        return result;
    }
}
