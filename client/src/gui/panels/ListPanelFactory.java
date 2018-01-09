package gui.panels;

import State.Chat;
import State.Friend;
import State.User;
import State.Room;
import constants.Dimensions;
import gui.Utils;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListPanelFactory {

    // Double click listener on online user or joined room start the chat
    private static MouseListener startChatListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent mouseEvent) {
            JList jlist = (JList) mouseEvent.getSource();
            if (mouseEvent.getClickCount() == 2) {
                int index = jlist.locationToIndex(mouseEvent.getPoint());
                if (index >= 0) {
                    String name = jlist.getModel().getElementAt(index).toString();
                    
                    // TODO: cercare un modo di capire se è stata cliccata la lista amici o la lista chat
                    boolean isFriendChat = true;
                    
                    Chat chat = isFriendChat ? User.getFriend(name) : User.getRoom(name);
                    chat.createWindow();
                }
            }
        }
    };

    // Double click listener on other room to join
    private static MouseListener addRoomListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent mouseEvent) {
            JList jlist = (JList) mouseEvent.getSource();
            if (mouseEvent.getClickCount() == 2) {
                int index = jlist.locationToIndex(mouseEvent.getPoint());
                if (index >= 0) {
                    String name = jlist.getModel().getElementAt(index).toString();
                    // TODO: add the room to my rooms
                    // TODO <antonio>: credo tu intenda di fare così:
                    User.getRoom(name).setStatus(true);
                }
            }
        }
    };

    private static JPanel preparePanel(JPanel firstPanel, JPanel secondPanel, JButton button) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(Dimensions.PADDING_BORDER);
        panel.add(firstPanel);
        panel.add(secondPanel);
        panel.add(button);
        return panel;
    }

    public static JPanel newFriendsPane(Collection<Friend> friends) {
        List<String> online = new ArrayList<>();
        List<String> offline = new ArrayList<>();
        for (Friend friend : friends) {
            if(friend.isOnline()) online.add(friend.getUsername());
            else offline.add(friend.getUsername());
        }
        online.sort(String::compareToIgnoreCase);
        offline.sort(String::compareToIgnoreCase);

        ListPanel onlinePanel = new ListPanel(
                "Online friends: double-click to open chat",
                online.toArray(new String[online.size()]),
                startChatListener
        );

        ListPanel offlinePanel = new ListPanel(
                "Offline friends",
                offline.toArray(new String[offline.size()]),
                null
        );

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel lookupPanel = new LookupPanel();
                Utils.createFixedWindow("Search for friend", lookupPanel, false, false);
            }
        };

        JButton button = new JButton("Add friend");
        button.addActionListener(action);

        return preparePanel(onlinePanel, offlinePanel, button);
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

        ListPanel subscriptionsPanel = new ListPanel(
                "My rooms: double-click to open chat",
                subscriptions.toArray(new String[subscriptions.size()]),
                startChatListener
        );

        ListPanel othersPanel = new ListPanel(
                "Other rooms",
                others.toArray(new String[others.size()]),
                addRoomListener
        );

        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel createRoomPanel = new CreateRoomPanel();
                Utils.createFixedWindow("Create new room", createRoomPanel, false, false);
            }
        };

        JButton button = new JButton("Create room");
        button.addActionListener(action);

        return preparePanel(subscriptionsPanel, othersPanel, button);
    }
}