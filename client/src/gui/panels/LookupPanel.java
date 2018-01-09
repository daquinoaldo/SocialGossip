package gui.panels;

import base.Json;
import base.State;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class LookupPanel extends JPanel {

    private JLabel resultLabel = new JLabel();
    private JButton addFriendButton = new JButton("Add");
    private String foundUser = null;

    LookupPanel() {
        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

        JTextField textField = new JTextField(30);
        textField.requestFocusInWindow();

        JButton searchButton = new JButton("Search");

        searchPanel.add(textField);
        searchPanel.add(searchButton);

        // Result panel
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.X_AXIS));

        addFriendButton.setEnabled(false);

        searchPanel.add(resultLabel);
        searchPanel.add(addFriendButton);

        // Add all to this
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(searchPanel);
        this.add(resultPanel);

        // Search listeners
        Action searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().equals("")) {
                    if(Json.lookup(textField.getText())) {
                        foundUser = textField.getText();
                        resultLabel.setText(foundUser+" found!");
                        addFriendButton.setEnabled(true);
                    } else {
                        resultLabel.setText("User not found.");
                        addFriendButton.setEnabled(false);
                        foundUser = null;
                    }
                    textField.setText("");
                }
            }
        };
        textField.addActionListener(searchAction);
        searchButton.addActionListener(searchAction);

        // Add friend listeners
        Action addAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (foundUser != null && Json.friendship(foundUser))
                    State.addFriend(foundUser, Json.isOnline(foundUser));
                for (State.Friend friend : State.friends())
                    System.out.println(friend.getUsername());
            }
        };
        addFriendButton.addActionListener(addAction);
    }

}
