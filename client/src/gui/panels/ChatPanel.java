package gui.panels;

import Connections.Connection;
import base.Json;
import base.State;
import base.State.Message;

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
        
        southPanel.add(msgField);
        southPanel.add(sendButton);
        
        // ChatPanel messages
        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        chatHistory.setLineWrap(true);
        
        JScrollPane scrollableChatMessages = new JScrollPane(chatHistory);
        scrollableChatMessages.getViewport().setViewPosition(new Point(0, chatHistory.getDocument().getLength()));
    
        this.add(scrollableChatMessages, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);

        // Send listeners
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!msgField.getText().equals("")) {
                    Message msg = new Message(State.username(), msgField.getText());
                    Json.sendMsg(username, msgField.getText());
                    chatHistory.append(msg.toString() + "\n");
                    msgField.setText("");
                }
            }
        };
        msgField.addActionListener(action);
        sendButton.addActionListener(action);

        // Msg listener
        State.addChatMsgListener(username, this::newMessage);
        //TODO: ma quando lo deregistro?????
    }
    
    private void newMessage(Message msg) {
        chatHistory.append(msg.toString() + "\n");
    }

}
