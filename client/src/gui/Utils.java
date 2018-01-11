package gui;

import constants.Icons;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Utils {

    public static void showErrorDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg,"Attenzione!", JOptionPane.WARNING_MESSAGE);
    }
    
    public static boolean showConfirmationDialog(String msg) {
        return JOptionPane.showConfirmDialog(null, msg,"Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    @SuppressWarnings("UnusedReturnValue")
    public static JFrame createWindow(String title, JPanel panel, Dimension dimension) {
        JFrame window = new JFrame(title);                                  // create a window
        window.setIconImage(Icons.logo.getImage());                         // set logo as application icon
        window.setSize(dimension);                                          // set specified size
        window.setContentPane(panel);                                       // put a panel inside the window
        window.setLocationRelativeTo(null);                                 // center the window
        window.setVisible(true);                                            // show it
        return window;
    }

    // crea una finestra centrata, non ridimensionabile, con il panel specificato
    public static JFrame createFixedWindow(String title, JPanel panel, boolean exitOnClose, boolean alwaysOnTop) {
        JFrame window = new JFrame(title);                                  // create a window
        window.setIconImage(Icons.logo.getImage());                         // set logo as application icon
        window.setContentPane(panel);                                       // put a panel inside the window
        window.pack();                                                      // resize the window based on content size
        window.setLocationRelativeTo(null);                                 // center the window
        if (exitOnClose)
            window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // exit program when window gets closed
        window.setAlwaysOnTop(alwaysOnTop);
        window.setResizable(false);                                         // unresizable window
        window.setVisible(true);                                            // show it
        return window;
    }

    public static void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container) component, enable);
            }
        }
    }
    
    public static File openFileDialog() {
        return fileDialog(true, null);
    }
    public static File saveFileDialog(String defaultName) {
        return fileDialog(false, defaultName);
    }
    
    private static File fileDialog(boolean isOpenDialog, String filename) {
        File selected = null;
        boolean aFileIsSelected = false;
    
        JFileChooser chooser = null;
        LookAndFeel previousLF = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            chooser = new JFileChooser();
            UIManager.setLookAndFeel(previousLF);
        } catch (IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException |
                ClassNotFoundException e) {
            e.printStackTrace();
        }
    
        if (chooser == null) chooser = new JFileChooser();
        if (filename != null) chooser.setSelectedFile(new File(filename));
        
        do {
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnStatus = isOpenDialog ? chooser.showOpenDialog(null) : chooser.showSaveDialog(null);
            
            if (returnStatus == JFileChooser.APPROVE_OPTION)
                selected = chooser.getSelectedFile();
            else continue;
    
            if (!isOpenDialog && selected.exists()) {
                aFileIsSelected = showConfirmationDialog("The file will be overwitten. Are you sure?");
            } else if (!isOpenDialog && selected.exists() && !selected.canWrite()) {
                showErrorDialog("Can't write in the specified path. Please try again.");
            } else if (isOpenDialog && !selected.canRead()) {
                showErrorDialog("Can't read the selected file. Please try again.");
            } else {
                aFileIsSelected = true;
            }
            
        } while (!aFileIsSelected);
        
        return selected;
    }
}
