import java.util.ArrayList;
import java.util.function.Consumer;

public class State {
    private static boolean isLoggedIn = false;
    private static String username = null;
    private static ArrayList<String> friends = new ArrayList<>();
    
    private static Consumer<Boolean> onLoggedInChange = null;
    private static Consumer<String> onUsernameChange = null;
    private static Consumer<ArrayList<String>> onFriendsChange = null;
    
    // State changes, will trigger a callback if any was set
    public static void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        if (onLoggedInChange != null) onLoggedInChange.accept(isLoggedIn);
    }
    
    public static void setUsername(String username) {
        State.username = username;
        if (onUsernameChange != null) onUsernameChange.accept(username);
    }
    
    public static void addFriend(String newFriend) {
        friends.add(newFriend);
        if (onFriendsChange != null) onFriendsChange.accept(friends);
    }
    
    // Basic setters for callbacks
    public static void onLogin(Consumer<Boolean> onLoggedInChange) {
        State.onLoggedInChange = onLoggedInChange;
    }
    
    public static void onUsernameChange(Consumer<String> onUsernameChange) {
        State.onUsernameChange = onUsernameChange;
    }
    
    public static void onFriendsChange(Consumer<ArrayList<String>> onFriendsChange) {
        State.onFriendsChange = onFriendsChange;
    }
}
