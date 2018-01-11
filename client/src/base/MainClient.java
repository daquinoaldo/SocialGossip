package base;

import connections.Connection;
import state.User;
import gui.Utils;
import gui.panels.LoginPanel;
import gui.panels.MainPanel;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

                // Every 1s refresh the room list
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                //scheduler.scheduleAtFixedRate(Json::chatList, 0, 5, TimeUnit.SECONDS);
            }
        });

        User.addUsernameListener(username -> {
            System.out.println("Logged in as " + username);
            if (mainWindow != null)
                mainWindow.setTitle(username + " - Social Gossip");
            rmi.Manager.registerCallback();
        });
    }
}