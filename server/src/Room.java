public class Room {

    private String name;
    private User creator;

    public Room(String name, User creator) {
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
