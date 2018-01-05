package gui;

import constants.Icons;

import javax.swing.*;
import java.awt.*;

public class Util {
    public static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg,"Attenzione!", JOptionPane.WARNING_MESSAGE);
    }
    
    public static JFrame createWindow(String title, JPanel panel, Dimension dimension) {
        JFrame window = new JFrame(title);                                  // create a window
        window.setIconImage(Icons.logo.getImage());                         // set logo as application icon
        window.setSize(dimension);                                          // set specified size
        window.setContentPane(panel);                                       // put a panel inside the window
        window.setLocationRelativeTo(null);                                 // center the window
        window.setVisible(true);                                            // show it
        return window;
    }
    
    // crea una finestra centrata, non ridimensionabile, con il panel specificato
    public static JFrame createFixedWindow(String title, JPanel panel, boolean exitOnClose, boolean alwaysOnTop) {
        JFrame window = new JFrame(title);                                  // create a window
        window.setIconImage(Icons.logo.getImage());                         // set logo as application icon
        window.setContentPane(panel);                                       // put a panel inside the window
        window.pack();                                                      // resize the window based on content size
        window.setLocationRelativeTo(null);                                 // center the window
        if (exitOnClose)
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // exit program when window gets closed
        window.setAlwaysOnTop(alwaysOnTop);
        window.setResizable(false);                                         // unresizable window
        window.setVisible(true);                                            // show it
        return window;
    }
    
    public static JFrame createFixedWindow(String title, JPanel panel) {
        return createFixedWindow(title, panel, false, false);
    }
    
    public static void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container) component, enable);
            }
        }
    }
}
