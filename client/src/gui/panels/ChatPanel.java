package gui.panels;

import State.*;
import base.Json;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class ChatPanel extends JPanel {
    private static JTextArea chatHistory;
    private static JTextField msgField;

    private static Action getSendMsgAction(Room room) {
        return new AbstractAction() {
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

    private static Action getSendMsgAction(Friend friend) {
        return new AbstractAction() {
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

    private static JButton getSecondButton(Room room) {
        if (room.getCreator().equals(User.username())) {
            JButton secondButton = new JButton("Close room");
            secondButton.addActionListener(e -> Json.closeRoom(room.getName()));
            return secondButton;
        }
        return null;
    }

    private static JButton getSecondButton(Friend friend) {
        JButton secondButton = new JButton("Attach file");
        secondButton.addActionListener(e -> Json.sendFileRequest(friend.getUsername()));
        return secondButton;
    }

    public ChatPanel(Room room) {
        this(getSendMsgAction(room), getSecondButton(room));
    }
    
    public ChatPanel(Friend friend) {
        this(getSendMsgAction(friend), getSecondButton(friend));
    }
    
    private ChatPanel(Action sendMsgAction, JButton secondButton) {
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
