package gui.panels;

import State.Message;
import base.Json;
import State.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class ChatPanel extends JPanel {

    private final JTextArea chatHistory;
    private final JTextField msgField;
    
    public ChatPanel(String username) {
        setLayout(new BorderLayout());
        
        // Send a new message input and button
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
    
        msgField = new JTextField(30);
        msgField.requestFocusInWindow();
        JButton sendButton = new JButton("Send");
        JButton fileButton = new JButton("Attach file");
        
        southPanel.add(msgField);
        southPanel.add(sendButton);
        southPanel.add(fileButton);
        
        // ChatPanel messages
        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        chatHistory.setLineWrap(true);
        
        JScrollPane scrollableChatMessages = new JScrollPane(chatHistory);
        scrollableChatMessages.getViewport().setViewPosition(new Point(0, chatHistory.getDocument().getLength()));
    
        this.add(scrollableChatMessages, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);

        // Send listeners
        Action sendMsgAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!msgField.getText().equals("")) {
                    Message msg = new Message(User.username(), msgField.getText());
                    Json.sendMsg(username, msgField.getText());
                    chatHistory.append(msg.toString() + "\n");
                    msgField.setText("");
                }
            }
        };
        msgField.addActionListener(sendMsgAction);
        sendButton.addActionListener(sendMsgAction);

        // Send listeners
        Action attachmentListener = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Json.sendFileRequest(username);
            }
        };
        fileButton.addActionListener(attachmentListener);
    }
    
    public void newMessage(Message msg) {
        chatHistory.append(msg.toString() + "\n");
    }

}
