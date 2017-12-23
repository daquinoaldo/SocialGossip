package gui.components;

import constants.Colors;
import gui.Util;

import javax.swing.*;
import java.awt.*;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginForm extends JPanel {
    private JTextField usernameInput = InputFactory.getTextInput("username", e -> submit());
    private JTextField passwordInput = InputFactory.getPasswordInput("password", e -> submit());
    private BiConsumer<String, String> loginCallback;
    
    public LoginForm(BiConsumer<String, String> loginCallback, Runnable registerCallback) {
        this.loginCallback = loginCallback;
        
        this.setBackground(Colors.background);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    
        JButton sendBtn = InputFactory.getMainButton("Login", e -> submit());
        JButton registerBtn = InputFactory.getSecondaryButton("Registrati", e -> registerCallback.run());
        
        JPanel btnsPanel = new JPanel();
        btnsPanel.setBackground(Colors.background);
        btnsPanel.add(sendBtn);
        btnsPanel.add(registerBtn);
    
        this.add(usernameInput);
        this.add(Box.createRigidArea(new Dimension(0, 5)));
        this.add(passwordInput);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(btnsPanel);
    }
    
    private void submit() {
        // Submit button or Enter key pressed
        String username = usernameInput.getText().trim();
        String password = passwordInput.getText(); // deprecated, use Char[] passwordInput.getPassword() in production
        
        if (isValidUsername(username) && password.length() > 0)
            this.loginCallback.accept(username, password);
        else {
            Util.showErrorDialog("Controllare i valori dei campi inseriti.");
            
        }
    }
    
    private boolean isValidUsername(String username) {
        Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(username);
    
        return username.length() > 0 && !m.find();
    }
    
    private boolean isValidPassword(String password) {
        return password.length() > 0;
    }
}
