package gui.panels;

import State.*;
import base.Json;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class ChatPanel extends JPanel {
    private JTextArea chatHistory;
    private JTextField msgField;
    
    private JButton secondButton;
    Action sendMsgAction;
    
    public ChatPanel(Room room) {
        this((Chat) room);

        if (room.getCreator().equals(User.username())) {
            JButton closeButton = new JButton("Close room");
            closeButton.addActionListener(e -> Json.closeRoom(room.getName()));
        }
    
        sendMsgAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!msgField.getText().equals("")) {
                    Message msg = new Message(User.username(), msgField.getText());
                    Json.sendChatMsg(room, msgField.getText());
                    chatHistory.append(msg.toString() + "\n");
                    msgField.setText("");
                }
            }
        };
    }
    
    public ChatPanel(Friend friend) {
        this((Chat) friend);

        JButton fileButton = new JButton("Attach file");
        fileButton.addActionListener(e -> Json.sendFileRequest(friend.getUsername()));
    
        sendMsgAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!msgField.getText().equals("")) {
                    Message msg = new Message(User.username(), msgField.getText());
                    Json.sendMsg(friend.getUsername(), msgField.getText());
                    chatHistory.append(msg.toString() + "\n");
                    msgField.setText("");
                }
            }
        };
    }
    
    private ChatPanel(Chat chat) {
        setLayout(new BorderLayout());
        
        // Send a new message input and button
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
    
        msgField = new JTextField(30);
        msgField.requestFocusInWindow();
        JButton sendButton = new JButton("Send");
        
        southPanel.add(msgField);
        southPanel.add(sendButton);
        if (secondButton != null) southPanel.add(secondButton);
        
        // ChatPanel messages
        chatHistory = new JTextArea();
        chatHistory.setEditable(false);
        chatHistory.setLineWrap(true);
        
        JScrollPane scrollableChatMessages = new JScrollPane(chatHistory);
        scrollableChatMessages.getViewport().setViewPosition(new Point(0, chatHistory.getDocument().getLength()));
    
        this.add(scrollableChatMessages, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);

        // Send listeners
        msgField.addActionListener(sendMsgAction);
        sendButton.addActionListener(sendMsgAction);
    }
    
    public void newMessage(Message msg) {
        chatHistory.append(msg.toString() + "\n");
    }

}
