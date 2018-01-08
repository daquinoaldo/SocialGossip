package gui.panels;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import constants.Dimensions;
import gui.Utils;

public class ListPanel extends JPanel {

    public ListPanel(String firstText, String[] firstGroup, String secondText, String[] secondGroup) {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setBorder(Dimensions.PADDING_BORDER);

        JList<String> firstList = new JList<>(firstGroup);
        JList<String> secondList = new JList<>(secondGroup);

        JScrollPane firstPanel = new JScrollPane(firstList);
        firstPanel.setAlignmentX(LEFT_ALIGNMENT);
        JScrollPane secondPanel = new JScrollPane(secondList);
        secondPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel firstLabel = new JLabel(firstText);
        JLabel secondLabel = new JLabel(secondText);

        this.add(firstLabel);
        this.add(Dimensions.V_SPACER);
        this.add(firstPanel);

        this.add(secondLabel);
        this.add(Dimensions.V_SPACER);
        this.add(secondPanel);

        // Double click listener for first group
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseEvent) {
                JList jlist = (JList) mouseEvent.getSource();
                if (mouseEvent.getClickCount() == 2) {
                    int index = jlist.locationToIndex(mouseEvent.getPoint());
                    if (index >= 0) {
                        String username = jlist.getModel().getElementAt(index).toString();

                        JPanel chatPanel = new ChatPanel();
                        Utils.createWindow(username, chatPanel, Dimensions.CHAT_PANE);
                    }
                }
            }
        };
        firstList.addMouseListener(mouseListener);

    }

}