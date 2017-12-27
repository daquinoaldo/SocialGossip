package base;

import gui.Util;
import gui.panels.Login;

import javax.swing.*;

public class MainClient {
    private static JFrame mainWindow = null;
    
    public static void main(String[] args) {
        // Mostrare finestra di login
        JPanel loginPanel = new Login();
        JFrame loginWindow = Util.createWindow("Social Gossip", loginPanel, true, false);
        
        State.onLogin(isLoggedIn -> {
            if (isLoggedIn) {
                loginWindow.dispose();
                JPanel mainPanel = new JPanel();
                mainWindow = Util.createWindow("Social Gossip", mainPanel, true, false);
            }
        });
        
        State.onUsernameChange(username -> {
            if (mainWindow != null)
                mainWindow.setTitle(username + " - Social Gossip");
        });
    }
}