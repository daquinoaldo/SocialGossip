import base.User;

public class XRoom {

    private String name;
    private User creator;

    public XRoom(String name, User creator) {
        this.name = name;
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public User getCreator() {
        return creator;
    }
}
