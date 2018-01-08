package base;

import Connections.Connection;
import constants.Dimensions;
import gui.Utils;
import gui.panels.ListPanelFactory;
import gui.panels.LoginPanel;

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

                JPanel friendsPane = ListPanelFactory.newFriendsPane(State.friends());
                Utils.createWindow("Friends", friendsPane, Dimensions.LIST_PANE, true);
                JPanel roomsPane = ListPanelFactory.newRoomsPane(State.rooms());
                Utils.createWindow("Friends", roomsPane, Dimensions.LIST_PANE, true);
            }
        });
        
        State.addUsernameListener(username -> {
            if (mainWindow != null)
                mainWindow.setTitle(username + " - Social Gossip");
            rmi.Manager.registerCallback();
        });
    }
}