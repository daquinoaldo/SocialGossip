package gui.components;

import static constants.Colors.background;
import static constants.Colors.accent;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

class ColorArrowUI extends BasicComboBoxUI {

    @Override protected JButton createArrowButton() {
        return new BasicArrowButton(
                BasicArrowButton.SOUTH,
                background, background,
                accent, background);
    }
}