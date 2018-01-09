package gui.panels;

import State.User;
import base.Json;

import javax.swing.*;
import java.awt.event.ActionEvent;

class CreateRoomPanel extends JPanel {

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
                        User.addRoom(textField.getText(), true);   //TODO: è sottoscritta?
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
