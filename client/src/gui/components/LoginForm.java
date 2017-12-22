package gui.components;

import constants.Colors;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginForm extends JPanel {
    private JTextField usernameInput = new JTextField("username");
    private JPasswordField passwordInput = new JPasswordField("password");
    private BiConsumer<String, String> loginCallback;
    
    private static final Border inputBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.accent, 1),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
    );
    
    // Set default setting for text input boxes
    private void setJTextOptions(JTextField field) {
        field.setBorder(inputBorder);                    // box border
        field.setForeground(Colors.accent);              // text color
        field.setHorizontalAlignment(JTextField.CENTER); // text align center
        field.addActionListener(e -> submit());
    }
    
    public LoginForm(BiConsumer<String, String> loginCallback, Runnable registerCallback) {
        this.loginCallback = loginCallback;
        
        this.setBackground(Colors.background);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        setJTextOptions(usernameInput);
        setJTextOptions(passwordInput);
        
        JButton sendBtn = new JButton("Login");
        sendBtn.addActionListener(e -> submit());
        sendBtn.setBackground(Colors.accent);
        sendBtn.setForeground(Colors.lightText);
    
        JButton registerBtn = new JButton("Registrati");
        registerBtn.addActionListener(e -> registerCallback.run());
        registerBtn.setBackground(Colors.background);
        registerBtn.setForeground(Colors.accent);
        registerBtn.setBorderPainted(false);
    
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
        String username = usernameInput.getText();
        String password = passwordInput.getText(); // deprecated, use Char[] passwordInput.getPassword() in production
        
        if (isValidUsername(username) && password.length() > 0)
            this.loginCallback.accept(username, password);
        else {
            JOptionPane.showMessageDialog(null,
                    "Controllare i valori dei campi.",
                    "Attenzione!",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private boolean isValidUsername(String username) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(username);
    
        return username.length() > 0 && !m.find();
    }
    
    private boolean isValidPassword(String password) {
        return password.length() > 0;
    }
}
