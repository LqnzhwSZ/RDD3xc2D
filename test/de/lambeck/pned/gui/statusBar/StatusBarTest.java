package de.lambeck.pned.gui.statusBar;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Locale;

import javax.swing.JFrame;

import de.lambeck.pned.i18n.I18NManager;

@SuppressWarnings("javadoc")
public class StatusBarTest {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(800, 600);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        I18NManager i18n = new I18NManager(new Locale("de", "DE"));
        contentPane.add(new StatusBar(i18n), BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
