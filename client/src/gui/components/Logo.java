package gui.components;

import constants.Colors;
import constants.Icons;

import javax.swing.*;
import java.awt.*;

public class Logo extends JPanel {
    public Logo() {
        this.setOpaque(true);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Colors.background);
    
        Image icon = Icons.logo.getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(icon), JLabel.CENTER);
        iconLabel.setMaximumSize(new Dimension(200, 128));
    
        JLabel title = new JLabel("Social Gossip");
        title.setMaximumSize(new Dimension(200, 128));
        title.setForeground(Colors.accent);
        title.setFont(new Font("Arial", Font.PLAIN, 26));
        title.setHorizontalAlignment(JLabel.CENTER);
    
        this.add(iconLabel);
        this.add(title);
    }
}
