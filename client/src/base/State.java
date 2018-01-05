package base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class State {
    public class Message {
        public String sender;
        public String text;
        public String toString() { return "<" + sender + ">: " + text; }
    }
    
    // State structure
    private static boolean isLoggedIn = false;
    private static String username = null;
    private static ArrayList<String> friends = new ArrayList<>();
    
    // Callbacks
    private static ArrayList<Consumer<Boolean>> loginCallbacks = new ArrayList<>();
    private static ArrayList<Consumer<String>> usernameCallbacks = new ArrayList<>();
    private static ArrayList<Consumer<ArrayList<String>>> friendsCallbacks = new ArrayList<>();
    private static HashMap<String, Consumer<Message>> chatMsgCallbacks = new HashMap<>();
    
    // Getters
    public static boolean isIsLoggedIn() { return isLoggedIn; }
    public static String username() { return username; }
    public static ArrayList<String> friends() { return friends; }
    
    // State changes, will trigger a callback if any was set
    public static void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        loginCallbacks.forEach(c -> c.accept(isLoggedIn));
    }
    
    public static void setUsername(String username) {
        State.username = username;
        usernameCallbacks.forEach(c -> c.accept(username));
    }
    
    public static void addFriend(String newFriend) {
        friends.add(newFriend);
        friendsCallbacks.forEach(c -> c.accept(friends));
    }
    
    public static void newMessage(String chatname, Message msg) {
        if (chatMsgCallbacks.containsKey(chatname)) {
            chatMsgCallbacks.get(chatname).accept(msg);
        }
    }
    
    // Basic setters for callbacks
    public static void addLoginListener(Consumer<Boolean> callback) {
        loginCallbacks.add(callback);
    }
    
    public static void addUsernameListener(Consumer<String> callback) {
        usernameCallbacks.add(callback);
    }
    
    public static void addFriendsListener(Consumer<ArrayList<String>> callback) {
        friendsCallbacks.add(callback);
    }
}
