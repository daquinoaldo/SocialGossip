package base;

import Connections.Connection;
import gui.Util;
import gui.panels.Login;
import gui.panels.MainPanel;

import javax.swing.*;
import java.net.Socket;

public class MainClient {
    private static JFrame mainWindow = null;
    
    public static void main(String[] args) {
        // Establish TCP connections
        Socket socket;
        try {
            socket = new Socket(Configuration.HOSTNAME, Configuration.PRIMARY_PORT);
        }
        catch (Exception e) {}
        Connection.init();
        
        // Show login window
        JPanel loginPanel = new Login();
        JFrame loginWindow = Util.createFixedWindow("Social Gossip", loginPanel, true, false);
        
        State.addLoginListener(isLoggedIn -> {
            if (isLoggedIn) {
                loginWindow.dispose();
                
                MainPanel mainPanel = new MainPanel();
                mainWindow = Util.createFixedWindow("Social Gossip", mainPanel, true, false);
            }
        });
        
        State.addUsernameListener(username -> {
            if (mainWindow != null)
                mainWindow.setTitle(username + " - Social Gossip");
        });
    }
}