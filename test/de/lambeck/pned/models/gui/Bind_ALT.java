package de.lambeck.pned.models.gui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/*
 * https://stackoverflow.com/q/8524874
 */
import javax.swing.*;

@SuppressWarnings("serial")
class Bind_ALT extends JPanel {
    {
        /*
         * https://www.java-forums.org/awt-swing/35240-key-binding-vk_alt-
         * vk_shift-not-working.html
         * 
         * "For modifier keys, you must provide the appropriate modifier for
         * KeyStrokes on key press. However, if you want the KeyStroke on key
         * release, then you do not include the modifier."
         */
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, InputEvent.ALT_DOWN_MASK, false), "pressed");
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0, true), "released");

        getActionMap().put("pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("ALT pressed");
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        });

        getActionMap().put("released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("ALT released");
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame f = new JFrame("Key Bindings");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.add(new Bind_ALT());
                f.setSize(640, 480);
                f.setVisible(true);
            }
        });
    }
}
