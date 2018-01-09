package gui.panels;

import base.State;

import javax.swing.*;
import java.util.Collection;

public class MainPanel extends JPanel {

    private JPanel friendsPane;
    private JPanel roomsPane;

    private void updateFriends(Collection<State.Friend> friends) {
        this.remove(friendsPane);
        friendsPane = ListPanelFactory.newFriendsPane(friends);
        this.add(friendsPane);
    }

    private void updateRooms(Collection<State.Room> room) {
        roomsPane = ListPanelFactory.newRoomsPane(room);
        this.invalidate();
    }

    public MainPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        friendsPane = ListPanelFactory.newFriendsPane(State.friends());
        roomsPane = ListPanelFactory.newRoomsPane(State.rooms());

        this.add(friendsPane);
        this.add(roomsPane);

        State.addFriendsListener(this::updateFriends);
        State.addChatsListener(this::updateRooms);
    }

}
