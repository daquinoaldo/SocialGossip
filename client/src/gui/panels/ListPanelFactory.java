package gui.panels;

import base.State.Friend;
import base.State.Room;
import constants.Dimensions;
import gui.Utils;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListPanelFactory {

    public static JPanel newFriendsPane(Collection<Friend> friends) {
        List<String> online = new ArrayList<>();
        List<String> offline = new ArrayList<>();
        for (Friend friend : friends) {
            if(friend.isOnline()) online.add(friend.getUsername());
            else offline.add(friend.getUsername());
        }
        online.sort(String::compareToIgnoreCase);
        offline.sort(String::compareToIgnoreCase);

        return new ListPanel(
                "Online: double-click to open chat",
                online.toArray(new String[online.size()]),
                "Offline",
                offline.toArray(new String[offline.size()])
        );
    }

    public static JPanel newRoomsPane(Collection<Room> rooms) {
        List<String> subscriptions = new ArrayList<>();
        List<String> others = new ArrayList<>();
        for (Room room : rooms) {
            if(room.isSubscribed()) subscriptions.add(room.getName());
            else others.add(room.getName());
        }
        subscriptions.sort(String::compareToIgnoreCase);
        others.sort(String::compareToIgnoreCase);

        return new ListPanel(
                "My rooms: double-click to open chat",
                subscriptions.toArray(new String[subscriptions.size()]),
                "Other rooms",
                others.toArray(new String[others.size()])
        );
    }


    // Test per la schermata di chat
    private static Collection<Friend> fakeFriends() {
        Collection<Friend> friends = new ArrayList<>();
        friends.add(new Friend("friend1", true));
        friends.add(new Friend("friend2", false));
        friends.add(new Friend("friend3", true));
        friends.add(new Friend("friend4", true));
        friends.add(new Friend("friend5", false));
        friends.add(new Friend("friend6", false));
        friends.add(new Friend("friend7", true));

        friends.add(new Friend("friend1", true));
        friends.add(new Friend("friend2", false));
        friends.add(new Friend("friend3", true));
        friends.add(new Friend("friend4", true));
        friends.add(new Friend("friend5", false));
        friends.add(new Friend("friend6", false));
        friends.add(new Friend("friend7", true));
        friends.add(new Friend("friend1", true));
        friends.add(new Friend("friend2", false));
        friends.add(new Friend("friend3", true));
        friends.add(new Friend("friend4", true));
        friends.add(new Friend("friend5", false));
        friends.add(new Friend("friend6", false));
        friends.add(new Friend("friend7", true));
        friends.add(new Friend("friend1", true));
        friends.add(new Friend("friend2", false));
        friends.add(new Friend("friend3", true));
        friends.add(new Friend("friend4", true));
        friends.add(new Friend("friend5", false));
        friends.add(new Friend("friend6", false));
        friends.add(new Friend("friend7", true));
        friends.add(new Friend("friend1", true));
        friends.add(new Friend("friend2", false));
        friends.add(new Friend("friend3", true));
        friends.add(new Friend("friend4", true));
        friends.add(new Friend("friend5", false));
        friends.add(new Friend("friend6", false));
        friends.add(new Friend("friend7", true));
        return friends;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { e.printStackTrace(); }
        });
        JPanel panel = ListPanelFactory.newFriendsPane(fakeFriends());
        Utils.createWindow("User list", panel, Dimensions.LIST_PANE);
    }
}