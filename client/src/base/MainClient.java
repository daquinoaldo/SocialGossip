package base;

import Connections.Connection;
import constants.Dimensions;
import gui.Utils;
import gui.panels.ListPanelFactory;
import gui.panels.LoginPanel;
import gui.panels.MainPanel;

import javax.swing.*;

class MainClient {
    private static JFrame mainWindow = null;
    
    public static void main(String[] args) {
        // Establish TCP connections
        Connection.init();
        
        // Show login window
        JPanel loginPanel = new LoginPanel();
        JFrame loginWindow = Utils.createFixedWindow("Social Gossip", loginPanel, true, false);
        
        State.addLoginListener(isLoggedIn -> {
            if (isLoggedIn) {
                loginWindow.dispose();

                JPanel mainPanel = new MainPanel();
                Utils.createFixedWindow("Social Gossip", mainPanel, true, false);
            }
        });

        State.addUsernameListener(username -> {
            if (mainWindow != null)
                mainWindow.setTitle(username + " - Social Gossip");
            rmi.Manager.registerCallback();
        });

        State.addFriendsListener(friends -> {
           //
        });


        //TEST
        State.addFriend("user1");
        State.addFriend("user2");
        State.addFriend("user3");
        State.addFriend("user4");
        State.addFriend("user5");
        State.setFriendStatus("user1", true);
        State.setFriendStatus("user3", true);
        State.setFriendStatus("user4", true);
        State.addRoom("room1");
        State.addRoom("room2");
        State.addRoom("room3");
        State.setLoggedIn(true);
        State.setUsername("aldo");
    }
}