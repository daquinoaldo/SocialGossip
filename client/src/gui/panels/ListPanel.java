package gui.panels;

import base.State;
import constants.Dimensions;
import gui.Utils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import base.State.Friend;

class ListPanel extends JPanel {

    private static final String DEFAULT_LABEL = "Double-click to start chat with...";

    private static String[] toArray(Collection<Friend> friends, boolean onlineOnly) {
        List<String> online = new ArrayList<>();
        List<String> offline = new ArrayList<>();
        for (Friend friend : friends) {
            if(friend.isOnline) online.add(friend.username);
            else offline.add(friend.username);
        }
        if (!onlineOnly) online.addAll(offline);
        return online.toArray(new String[online.size()]);
    }

    ListPanel(Collection<Friend> friends, boolean onlineOnly) {
        this(DEFAULT_LABEL, friends, onlineOnly);
    }
    ListPanel(String labelText, Collection<Friend> friends, boolean onlineOnly) {
        this(labelText, toArray(friends, onlineOnly));
    }
    ListPanel(String labelText, String[] chats) {
        JList<String> jlist = new JList<>(chats);
        JScrollPane listScroller = new JScrollPane(jlist);
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        JLabel label = new JLabel(labelText);
        this.add(label);
        this.add(Dimensions.V_SPACER);
        this.add(listScroller);
        this.setBorder(Dimensions.PADDING_BORDER);

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                JList jlist = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = jlist.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        String username = jlist.getModel().getElementAt(index).toString();

                        JPanel chatPanel = new ChatPanel();
                        Utils.createWindow(username, chatPanel, Dimensions.CHAT_PANE);
                    }
                }
            }
        };
        jlist.addMouseListener(mouseListener);

    }
    
    // Test per la schermata di chat
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { e.printStackTrace(); }
        });
        String[] chats = {"Ciao", "Prova", "Zaphodias", "daquinoaldo"};
        //JPanel panel = new ListPanel(chats);
        for(String chat : chats)
            State.addFriend(chat);
        JPanel panel = new ListPanel(State.friends(), true);
        Utils.createWindow("Chat list", panel, Dimensions.LIST_PANE);
    }
}