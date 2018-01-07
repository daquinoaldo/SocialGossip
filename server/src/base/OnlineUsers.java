package base;

import java.net.Socket;
import java.util.*;

public class OnlineUsers {
    private static final Database db = new Database();
    private static final HashMap<String, User> users = new HashMap<>();
    
    public static boolean add(User user) {
        return users.putIfAbsent(user.getUsername(), user) == null;
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
            if (user.getPrimarySocket().equals(s) || user.getMessageSocket().equals(s))
                return user;
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
