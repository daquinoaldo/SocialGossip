package gui.components;

import base.TriConsumer;
import constants.Colors;
import gui.Utils;

import javax.swing.*;
import java.awt.*;

import static gui.Validators.isValidPassword;
import static gui.Validators.isValidUsername;

public class RegisterForm extends JPanel {
    private static final String[] languageList = {
        "it", "en", "de", "fr"
    };
    
    private final TriConsumer<String, String, String> registerCallback;
    private final JTextField usernameInput = InputFactory.getTextInput("username", e -> submit());
    private final JTextField passwordInput = InputFactory.getPasswordInput("password", e -> submit());
    private final JTextField passwordInput2 = InputFactory.getPasswordInput("password", e -> submit());
    private final JComboBox languageInput = InputFactory.getComboBox(languageList, e -> submit());
    
    public RegisterForm(TriConsumer<String, String, String> registerCallback) {
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
        this.add(languageInput);
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
        String language = languageInput.getActionCommand();
        
        if (isValidUsername(username) && isValidPassword(password) && password.equals(password2)) {
            registerCallback.accept(username, password, language);
        }
        else {
            Utils.showErrorDialog("I dati inseriti non sono corrretti.");
        }
    }
}
