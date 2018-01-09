
package base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class State {
    // Sub-classes
    public static class Friend {
        private String username;
        private boolean online = false;
        public Friend(String username) { this.username = username; }
        public Friend(String username, boolean online) {
            this(username);
            this.online = online;
        }
        public void setStatus(boolean online) { this.online = online; }
        public String getUsername() { return username; }
        public boolean isOnline() { return online; }
    }

    public static class Room {
        private String name;
        private boolean subscribed = false;
        public Room(String name) { this.name = name; }
        public Room(String name, boolean subscribed) {
            this(name);
            this.subscribed = subscribed;
        }
        public void setStatus(boolean subscribed) { this.subscribed = subscribed; }
        public String getName() { return name; }
        public boolean isSubscribed() { return subscribed; }
    }
    
    public static class Message {
        final String sender;
        final String text;
        public Message(String sender, String text) {
            this.sender = sender;
            this.text = text;
        }
        public String toString() { return "<" + sender + ">: " + text; }
    }
    
    // State structure
    private static boolean isLoggedIn = false;
    private static String username = null;
    private static final HashMap<String, Friend> friends = new HashMap<>();
    private static final HashMap<String, Room> rooms = new HashMap<>();
    
    // Callbacks
    private static final ArrayList<Consumer<Boolean>> loginCallbacks = new ArrayList<>();
    private static final ArrayList<Consumer<String>> usernameCallbacks = new ArrayList<>();
    private static final ArrayList<Consumer<Collection<Friend>>> friendsListCallbacks = new ArrayList<>();
    private static final ArrayList<Consumer<Collection<Room>>> chatsListCallbacks = new ArrayList<>();
    private static final HashMap<String, Consumer<Message>> chatMsgCallbacks = new HashMap<>(); // one callback per chat only
    
    // Getters
    public static boolean isIsLoggedIn() { return isLoggedIn; }
    public static String username() { return username; }
    public static Collection<Friend> friends() { return friends.values(); }
    public static Collection<Room> rooms() { return rooms.values(); }
    
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
        friendsListCallbacks.forEach(c -> c.accept(friends.values()));
    }
    
    public static void addFriend(String friendUsername) {
        friends.put(friendUsername, new Friend(friendUsername));
        friendsListCallbacks.forEach(c -> c.accept(friends.values()));
    }

    public static void setFriendList(List<Friend> friends) {
        State.friends.clear();
        for (Friend friend : friends)
            State.friends.put(friend.getUsername(), friend);
    
        friendsListCallbacks.forEach(c -> c.accept(friends));
    }

    public static void addRoom(String roomName) {
        rooms.put(roomName, new Room(roomName));
        chatsListCallbacks.forEach(c -> c.accept(rooms.values()));
    }

    public static void setRoomList(List<Room> rooms) {
        State.rooms.clear();
        for (Room room : rooms)
            State.rooms.put(room.getName(), room);
        chatsListCallbacks.forEach(c -> c.accept(rooms));
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

    public static void addChatsListener(Consumer<Collection<Room>> callback) {
        chatsListCallbacks.add(callback);
    }

    public static void addChatMsgListener(String chatname, Consumer<Message> callback) {
        chatMsgCallbacks.put(chatname, callback);
    }
}
