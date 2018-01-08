package gui.panels;

import javax.swing.*;
import java.awt.event.MouseListener;

import constants.Dimensions;

public class ListPanel extends JPanel {

    public ListPanel(String text, String[] chats, MouseListener mouseListener) {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setBorder(Dimensions.V_PADDING_BORDER);

        JLabel label = new JLabel(text);

        JList<String> list = new JList<>(chats);
        if (mouseListener != null) list.addMouseListener(mouseListener);

        JScrollPane panel = new JScrollPane(list);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        this.add(label);
        this.add(Dimensions.V_SPACER);
        this.add(panel);

    }

}