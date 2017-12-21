package GUI;

import constants.Colors;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel implements ActionListener {
    private JTextField nicknameInput = new JTextField("username");
    private JPasswordField passwordInput = new JPasswordField("password");
    
    public LoginPanel() {
        setBackground(Colors.background);
        int widthBorder = 65;
        setBorder(BorderFactory.createEmptyBorder(35, widthBorder,35, widthBorder));

        // Logo (icon and title)
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(true);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(Colors.background);
        
        Image icon = (new ImageIcon("Client/media/logo.png")).getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(icon), JLabel.CENTER);
        iconLabel.setMaximumSize(new Dimension(200, 128));
        
        JLabel title = new JLabel("Social Gossip");
        title.setMaximumSize(new Dimension(200, 128));
        title.setForeground(Colors.accent);
        title.setFont(new Font("Arial", Font.PLAIN, 26));
        title.setHorizontalAlignment(JLabel.CENTER);
    
        logoPanel.add(iconLabel);
        logoPanel.add(title);
        
        // Login form
        JPanel loginFormPanel = new JPanel();
        loginFormPanel.setBackground(Colors.background);
        loginFormPanel.setLayout(new BoxLayout(loginFormPanel, BoxLayout.Y_AXIS));
    
        Border textBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.accent, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        );
        
        nicknameInput.setBorder(textBorder);
        passwordInput.setBorder(textBorder);
    
        nicknameInput.setForeground(Colors.accent);
        passwordInput.setForeground(Colors.accent);
        
    
        nicknameInput.setHorizontalAlignment(JTextField.CENTER); // text align center
        passwordInput.setHorizontalAlignment(JTextField.CENTER);
        
        JPanel btnsPanel = new JPanel();
        btnsPanel.setBackground(Colors.background);
        JButton sendBtn = new JButton("Login");
        sendBtn.setBackground(Colors.accent);
        sendBtn.setForeground(Colors.lightText);
        
        JButton registerBtn = new JButton("Registrati");
        registerBtn.setBackground(Colors.background);
        registerBtn.setForeground(Colors.accent);
        registerBtn.setBorderPainted(false);
        
        btnsPanel.add(sendBtn);
        btnsPanel.add(registerBtn);
        
        loginFormPanel.add(nicknameInput);
        loginFormPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        loginFormPanel.add(passwordInput);
        loginFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginFormPanel.add(btnsPanel);
        
        // finalizing panel
        setLayout(new GridLayout(2,1,3,50));
        add(logoPanel);
        add(loginFormPanel);
    
    }
    
    public static void main(String[] args) {
        JFrame window = new JFrame("Social Gossip - Login");
    
        LoginPanel content = new LoginPanel();
        window.setContentPane(content);
        
        window.pack();
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    
        window.setResizable(false);
        window.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("e = " + e);
    }
}
