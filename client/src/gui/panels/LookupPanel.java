package gui.panels;

import base.State;
import constants.Colors;
import gui.components.Logo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LookupPanel extends JPanel {
    private static final Dimension padding = new Dimension(65, 35);


    public LookupPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JTextField textField = new JTextField(30);
        textField.requestFocusInWindow();
        JButton button = new JButton("Search");

        this.add(textField);
        this.add(button);

        // Send listeners
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().equals("")) {
                    // TODO: lookup(textField.getText())
                    textField.setText("");
                }
            }
        };
        textField.addActionListener(action);
        button.addActionListener(action);
    }
}
