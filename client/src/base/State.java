
package base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Consumer;

public class State {
    // Sub-classes
    public static class Friend {
        private String username;
        private boolean online = false;
        public Friend(String username) { this.username = username; }
        public void setStatus(boolean online) { this.online = online; }
        public String getUsername() { return username; }
        public boolean isOnline() { return online; }
    }
    
    public static class Message {
        final String sender;
        final String text;
        public Message(String username, String text) { this.sender = username; this.text = text; }
        public String toString() { return "<" + sender + ">: " + text; }
    }
    
    // State structure
    private static boolean isLoggedIn = false;
    private static String username = null;
    private static final HashMap<String, Friend> friends = new HashMap<>();
    private static final ArrayList<String> rooms = new ArrayList<>();
    
    // Callbacks
    private static final ArrayList<Consumer<Boolean>> loginCallbacks = new ArrayList<>();
    private static final ArrayList<Consumer<String>> usernameCallbacks = new ArrayList<>();
    private static final ArrayList<Consumer<Collection<Friend>>> friendsListCallbacks = new ArrayList<>();
    private static final HashMap<String, Consumer<Boolean>> friendStatusCallback = new HashMap<>();
    private static final HashMap<String, Consumer<Message>> chatMsgCallbacks = new HashMap<>(); // one callback per chat only
    
    // Getters
    public static boolean isIsLoggedIn() { return isLoggedIn; }
    public static String username() { return username; }
    public static Collection<Friend> friends() { return friends.values(); }
    public static ArrayList<String> rooms() { return rooms; }
    
    // State changes, will trigger a callback if any was set
    public static void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        loginCallbacks.forEach(c -> c.accept(isLoggedIn));
    }
    
    public static void setUsername(String username) {
        State.username = username;
        usernameCallbacks.forEach(c -> c.accept(username));
    }
    
    public static void setFriendStatus(String username, boolean isOnline) {
        friends.get(username).setStatus(isOnline);
        if (friendStatusCallback.containsKey(username)) friendStatusCallback.get(username).accept(isOnline);
        friendsListCallbacks.forEach(c -> c.accept(friends.values()));
    }
    
    public static void addFriend(String friendUsername) {
        friends.put(friendUsername, new Friend(friendUsername));
        friendsListCallbacks.forEach(c -> c.accept(friends.values()));
    }

    public static void addRoom(String newRoom) {
        rooms.add(newRoom);
        //TODO: roomsCallbacks.forEach(c -> c.accept(rooms));
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
    
    public static void addFriendsListener(Consumer<Collection<Friend>> callback) {
        friendsListCallbacks.add(callback);
    }

    public static void addChatMsgListener(String chatname, Consumer<Message> callback) {
        chatMsgCallbacks.put(chatname, callback);
    }
    
    public static void addFriendStatusListener(String username, Consumer<Boolean> callback) {
        friendStatusCallback.put(username, callback);
    }
}
