package base;

import gui.Util;
import gui.panels.Login;
import gui.panels.MainPanel;

import javax.swing.*;

public class MainClient {
    private static JFrame mainWindow = null;
    
    public static void main(String[] args) {
        // Mostrare finestra di login
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