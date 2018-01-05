package remoteinterfaces;

public interface ClientCallbackInterface {
    void newFriend(String username);
    void changedStatus(String username, boolean isOnline);
}
