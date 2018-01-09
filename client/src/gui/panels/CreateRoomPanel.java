package gui.panels;

import base.Json;
import base.State;

import javax.swing.*;
import java.awt.event.ActionEvent;

class CreateRoomPanel extends JPanel {

    private JLabel resultLabel = new JLabel();
    private JButton addFriendButton = new JButton("Add");
    private String foundUser = null;

    CreateRoomPanel() {
        // Search panel
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JTextField textField = new JTextField(30);
        textField.requestFocusInWindow();

        JButton button = new JButton("Create");

        // Search listeners
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().equals("")) {
                    if(Json.createRoom(textField.getText()))
                        State.addRoom(textField.getText(), true);   //TODO: è sottoscritta?
                    textField.setText("");
                }
            }
        };
        textField.addActionListener(action);
        button.addActionListener(action);

        this.add(textField);
        this.add(button);
    }

}
