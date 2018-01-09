package gui.panels;

import base.State;

import javax.swing.*;

public class MainPanel extends JPanel {

    public MainPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel friendsPane = ListPanelFactory.newFriendsPane(State.friends());
        JPanel roomsPane = ListPanelFactory.newRoomsPane(State.rooms());

        this.add(friendsPane);
        this.add(roomsPane);
    }

}
