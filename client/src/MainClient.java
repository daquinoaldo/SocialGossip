import gui.Util;
import gui.panels.Login;

import javax.swing.*;

public class MainClient {
    public static void main(String[] args) {
        // Mostrare finestra di login
        JPanel loginPanel = new Login();
        JFrame loginWindow = Util.createWindow("Social Gossip", loginPanel, true, false);
    }
}