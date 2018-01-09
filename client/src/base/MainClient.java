package base;

import Connections.Connection;
import State.User;
import gui.Utils;
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
        
        User.addLoginListener(isLoggedIn -> {
            if (isLoggedIn) {
                loginWindow.dispose();

                JPanel mainPanel = new MainPanel();
                mainWindow = Utils.createFixedWindow("Social Gossip", mainPanel, true, false);
            }
        });

        User.addUsernameListener(username -> {
            if (mainWindow != null)
                mainWindow.setTitle(username + " - Social Gossip");
            rmi.Manager.registerCallback();
        });
    }
}