public class ChatRoom {

    public class ChatNotExistException extends RuntimeException { }
    public class AllUSersOfflineException extends  RuntimeException { }

    private User creator;
    private String chatName;

    public ChatRoom(User creator, String chatName) {
        this.creator = creator;
        this.chatName = chatName;
    }

    public String getChatName() {
        return chatName;
    }

    public User getCreator() {
        return creator;
    }
}
