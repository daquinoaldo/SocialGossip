package gui.panels;

import State.Friend;
import State.User;
import State.Room;

import javax.swing.*;
import java.util.Collection;

public class MainPanel extends JPanel {

    private JPanel friendsPane;
    private JPanel roomsPane;

    private void updateFriends(Collection<Friend> friends) {
        this.remove(friendsPane);
        this.remove(roomsPane);
        friendsPane = ListPanelFactory.newFriendsPane(friends);
        this.add(friendsPane);
        this.add(roomsPane);
        this.revalidate();
        this.repaint();
    }

    private void updateRooms(Collection<Room> room) {
        this.remove(friendsPane);
        this.remove(roomsPane);
        roomsPane = ListPanelFactory.newRoomsPane(room);
        this.add(friendsPane);
        this.add(roomsPane);
        this.revalidate();
        this.repaint();
    }

    public MainPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        friendsPane = ListPanelFactory.newFriendsPane(User.friends());
        roomsPane = ListPanelFactory.newRoomsPane(User.rooms());

        this.add(friendsPane);
        this.add(roomsPane);

        User.addFriendsListener(this::updateFriends);
        User.addChatsListener(this::updateRooms);
    }

}
