package gui.panels;

import constants.Colors;
import gui.components.LoginForm;
import gui.components.Logo;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class Login extends JPanel {
    private static final Dimension padding = new Dimension(65, 35);
    
    public Login(BiConsumer<String, String> loginCallback, Runnable registerCallback) {
        // Init components
        JPanel logoPanel = new Logo(); // icon and title
        JPanel loginFormPanel = new LoginForm(loginCallback, registerCallback); // form, submit and register buttons
    
        // Making the panel
        setBackground(Colors.background);
        setBorder(BorderFactory.createEmptyBorder(padding.height, padding.width,padding.height, padding.width));
        setLayout(new GridLayout(2,1,3,50));
        add(logoPanel);
        add(loginFormPanel);
    }
}
