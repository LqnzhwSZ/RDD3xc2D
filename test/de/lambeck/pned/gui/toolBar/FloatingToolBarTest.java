package de.lambeck.pned.gui.toolBar;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToolBar;

@SuppressWarnings("javadoc")
public class FloatingToolBarTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JToolBar toolbar = new JToolBar();
        toolbar.add(new JButton("Button 1"));
        toolbar.add(new JButton("Button 2"));

        Container contentPane = frame.getContentPane();
        contentPane.add(toolbar, BorderLayout.NORTH);

        // Hier passiert es!
        toolbar.setBounds(50, 50, 200, 50);

        frame.setSize(600, 400);
        frame.setVisible(true);
    }
}
