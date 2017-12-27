package gui.components;

import constants.Colors;
import gui.Util;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

import static gui.Validators.isValidPassword;
import static gui.Validators.isValidUsername;

public class RegisterForm extends JPanel {
    private final BiConsumer<String, String> registerCallback;
    private JTextField usernameInput = InputFactory.getTextInput("username", e -> submit());
    private JTextField passwordInput = InputFactory.getPasswordInput("password", e -> submit());
    private JTextField passwordInput2 = InputFactory.getPasswordInput("password", e -> submit());
    
    public RegisterForm(BiConsumer<String, String> registerCallback) {
        this.registerCallback = registerCallback;
        this.setBackground(Colors.background);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JButton registerBtn = InputFactory.getMainButton("Registrati", e -> submit());
        
        JPanel btnsPanel = new JPanel();
        btnsPanel.setBackground(Colors.background);
        btnsPanel.add(registerBtn);
        
        this.add(usernameInput);
        this.add(Box.createRigidArea(new Dimension(0, 5)));
        this.add(passwordInput);
        this.add(Box.createRigidArea(new Dimension(0, 5)));
        this.add(passwordInput2);
        this.add(Box.createRigidArea(new Dimension(0, 15)));
        this.add(btnsPanel);
    }
    
    private void submit() {
        // validate username
        // validate password
        // if successful run this.registerCallback(username, password)
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        String password2 = passwordInput2.getText();
        
        if (isValidUsername(username) && isValidPassword(password) && password.equals(password2)) {
            registerCallback.accept(username, password);
        }
        else {
            Util.showErrorDialog("I dati inseriti non sono corrretti.");
        }
    }
}
