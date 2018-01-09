
package State;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

// Main State structure - representing the User who is using the client
// if a state change is triggered, previously registered callbacks will be called passing the new state as a parameter

/*
 *   1. State change request      ->      2. internal data structure change        ->      3. callbacks are called with new state
 * (ex. user adds a new friend)        (this class private fields gets updated)          (GUI components can self-update themselves)
 *
 *  This strategy allows easier debugging and testing (it's possible to programmatically request a state change and log
 *  the resulted changes). But also simpler GUI components who just need to access this single central State container
 *  and can update their informations accordingly registering callbacks.
 */

public class User {
    private static boolean isLoggedIn = false;
    private static String username = null;
    private static final HashMap<String, Friend> friends = new HashMap<>();
    private static final HashMap<String, Room> rooms = new HashMap<>();
    
    // Callbacks store
    private static final ArrayList<Consumer<Boolean>> loginCallbacks = new ArrayList<>();
    private static final ArrayList<Consumer<String>> usernameCallbacks = new ArrayList<>();
    private static final ArrayList<Consumer<Collection<Friend>>> friendsListCallbacks = new ArrayList<>();
    private static final ArrayList<Consumer<Collection<Room>>> chatsListCallbacks = new ArrayList<>();
    
    // Getters
    public static boolean isLoggedIn() { return isLoggedIn; }
    public static String username() { return username; }
    public static Collection<Friend> friends() { return friends.values(); }
    public static Friend getFriend(String username) { return friends.get(username); }
    public static Collection<Room> rooms() { return rooms.values(); }
    public static Room getRoom(String name) { return rooms.get(name); }
    
    // User changes, will trigger a callback if any was set
    public static void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        loginCallbacks.forEach(c -> c.accept(isLoggedIn));
    }
    
    public static void setUsername(String username) {
        User.username = username;
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
    
    public static void addFriend(String friendUsername, boolean isOnline) {
        friends.put(friendUsername, new Friend(friendUsername, isOnline));
        friendsListCallbacks.forEach(c -> c.accept(friends.values()));
    }

    public static void setFriendList(List<Friend> friends) {
        User.friends.clear();
        for (Friend friend : friends)
            User.friends.put(friend.getUsername(), friend);
    
        friendsListCallbacks.forEach(c -> c.accept(friends));
    }

    public static void addRoom(String roomName) {
        rooms.put(roomName, new Room(roomName));
        chatsListCallbacks.forEach(c -> c.accept(rooms.values()));
    }

    public static void addRoom(String roomName, boolean subscribed) {
        rooms.put(roomName, new Room(roomName, subscribed));
        chatsListCallbacks.forEach(c -> c.accept(rooms.values()));
    }

    public static void setRoomList(List<Room> rooms) {
        User.rooms.clear();
        for (Room room : rooms)
            User.rooms.put(room.getName(), room);
        chatsListCallbacks.forEach(c -> c.accept(rooms));
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
    
}
