package gui.panels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LookupPanel extends JPanel {

    public LookupPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

        JTextField textField = new JTextField(30);
        textField.requestFocusInWindow();

        JButton button = new JButton("Search");

        searchPanel.add(textField);
        searchPanel.add(button);

        this.add(searchPanel);

        // Send listeners
        Action action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField.getText().equals("")) {
                    // TODO: lookup(textField.getText())
                    String[] results = new String[] {textField.getText()};   // TODO: Risultato di lookup
                    ListPanel listPanel = new ListPanel("Double-click to add a friend", results, addFriendListener);
                    LookupPanel thiz = (LookupPanel) textField.getParent().getParent();
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(thiz);
                    thiz.add(listPanel);
                    frame.pack();
                    thiz.revalidate();
                    thiz.repaint();
                    // TODO: non è allineato a sinistra
                    textField.setText("");
                }
            }
        };
        textField.addActionListener(action);
        button.addActionListener(action);
    }

    // Double click listener on other room to join
    private static MouseListener addFriendListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent mouseEvent) {
            JList jlist = (JList) mouseEvent.getSource();
            if (mouseEvent.getClickCount() == 2) {
                int index = jlist.locationToIndex(mouseEvent.getPoint());
                if (index >= 0) {
                    String friendname = jlist.getModel().getElementAt(index).toString();
                    // TODO: add the friend
                }
            }
        }
    };
}
