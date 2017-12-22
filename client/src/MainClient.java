import constants.Icons;
import gui.panels.Login;

import javax.swing.*;

public class MainClient {
    public static void main(String[] args) {
        // Mostrare finestra di login
        JPanel loginPanel = new Login(MainClient::loginCallback, MainClient::registerCallback);
        JFrame loginWindow = createWindow("Social Gossip", loginPanel);
    }
    
    private static void registerCallback() {
        System.out.println("Premuto bottone Registrati");
    }
    
    private static void loginCallback(String username, String password) {
        System.out.println("Login, username = " + username + ", password = " + password);
    }
    
    // crea una finestra centrata, non ridimensionabile, con il panel specificato
    private static JFrame createWindow(String title, JPanel panel) {
        JFrame window = new JFrame(title);                              // create a window
        window.setIconImage(Icons.logo.getImage());                     // set logo as application icon
        window.setContentPane(panel);                                   // put a panel inside the window
        window.pack();                                                  // resize the window based on content size
        window.setLocationRelativeTo(null);                             // center the window
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // exit program when window gets closed
        window.setResizable(false);                                     // unresizable window
        window.setVisible(true);                                        // show it
        return window;
    }
}