package gui.components;

import constants.Colors;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

public class InputFactory {
    /* Configuration */
    private static final Border inputBorder = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colors.accent, 1),
            BorderFactory.createEmptyBorder(2, 10, 2, 10)
    );
    private static final Color inputTextColor = Colors.accent;
    
    private static final Color mainButtonBackground = Colors.accent;
    private static final Color mainButtonText = Colors.lightText;
    private static final Color secondaryButtonBackground = Colors.background;
    private static final Color secondaryButtonText = Colors.accent;
    
    /* End configuration */
    
    /* Text inputs */
    private static JTextField makeField(String defaultText, ActionListener e, boolean isPassword) {
        JTextField field = isPassword ? new JPasswordField(defaultText) : new JTextField(defaultText);
        field.setBorder(inputBorder);                    // box border
        field.setForeground(inputTextColor);              // text color
        field.setHorizontalAlignment(JTextField.CENTER); // text align center
        field.addActionListener(e);
    
        return field;
    }
    
    public static JTextField getTextInput(String defaultText, ActionListener e) {
        return makeField(defaultText, e, false);
    }
    
    public static JTextField getPasswordInput(String defaultText, ActionListener e) {
        return makeField(defaultText, e, true);
    }
    
    /* Buttons */
    private static JButton makeButton(String text, ActionListener e, Color backgroundColor, Color textColor, boolean paintBorders) {
        JButton btn = new JButton(text);
        btn.addActionListener(e);
        btn.setBackground(backgroundColor);
        btn.setForeground(textColor);
        btn.setBorderPainted(paintBorders);
        return btn;
    }
    
    public static JButton getMainButton(String text, ActionListener e) {
        return makeButton(text, e, mainButtonBackground, mainButtonText, true);
    }
    
    public static JButton getSecondaryButton(String text, ActionListener e) {
        return makeButton(text, e, secondaryButtonBackground, secondaryButtonText, false);
    }
}
