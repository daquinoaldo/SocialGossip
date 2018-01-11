package gui.panels;

import state.*;
import base.Json;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.BiConsumer;


public class ChatPanel extends JPanel {
    private final JPanel southPanel = new JPanel();
    private final JTextArea chatHistory;
    private final JTextField msgField;
    
    private Chat chat;

    public ChatPanel(Room room) {
        this(Json::sendChatMsg);
        this.chat = room;
    
        if (room.getCreator().equals(User.username())) {
            JButton btn = new JButton("Close room");
            btn.addActionListener(e -> Json.closeRoom(room.getName()));
            addSecondButton(btn);
        }
    }
    
    public ChatPanel(Friend friend) {
        this(Json::sendMsg);
        this.chat = friend;
        
        JButton btn = new JButton("Attach file");
        btn.addActionListener(e -> {
            if (friend == null) return;
            Json.sendFileRequest(friend.getUsername());
        });
        addSecondButton(btn);
    }
    
    private void addSecondButton(JButton btn) {
        southPanel.add(btn);
        this.remove(southPanel);
        this.add(southPanel, BorderLayout.SOUTH);
        //msgField.requestFocusInWindow();
    }
    
    private ChatPanel(BiConsumer<String, String> sendMsgAction) {
        setLayout(new BorderLayout());
        
        // Send a new message input and button
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
        ActionListener eventHandler = (e) -> {
            String text = msgField.getText();
            if (!text.equals("")) {
                this.newMessage(new Message(User.username(), text));
                sendMsgAction.accept(chat.getName(), text);
                msgField.setText("");
            }
        };
        msgField.addActionListener(eventHandler);
        sendButton.addActionListener(eventHandler);
    }
    
    public void newMessage(Message msg) {
        chatHistory.append(msg.toString() + "\n");
    }
    
    public void focusGained() {
        msgField.requestFocusInWindow();
    }

}
