package gui.panels;

import base.State;
import gui.Utils;

import javax.swing.*;
import java.awt.*;
import java.net.InetAddress;

class ChatPanel extends JPanel {
    private InetAddress multicastAddress;
    
    private final JTextArea chatHistory;
    private final JTextField msgField;
    
    ChatPanel(String chatName) {
        setLayout(new BorderLayout());
        
        // Send a new message input and button
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
    
        msgField = new JTextField(30);
        msgField.requestFocusInWindow();
        JButton sndBtn = new JButton("Send");
        
        southPanel.add(msgField);
        southPanel.add(sndBtn);
        
        // ChatPanel messages
        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        chatHistory.setLineWrap(true);
        
        JScrollPane scrollableChatMessages = new JScrollPane(chatHistory);
        scrollableChatMessages.getViewport().setViewPosition(new Point(0, chatHistory.getDocument().getLength()));
    
        this.add(scrollableChatMessages, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }
    
    private void newMessage(State.Message msg) {
        chatHistory.append(msg.toString() + "\n");
    }
    
    // Test per la schermata di chat
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { e.printStackTrace(); }
        });
    
        JPanel panel = new ChatPanel("Serie A");
        
        Utils.createWindow("Serie A", panel, new Dimension(600, 400));
    }
}
