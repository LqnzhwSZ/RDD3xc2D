package de.lambeck.pned.filesystem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.*;

import de.lambeck.pned.application.actions.FileOpenAction;

@SuppressWarnings({ "javadoc", "serial" })
public class FileManagerTest_2 extends JPanel {

    private JFileChooser fileChooser = new JFileChooser();
    private Action fileOpenAction = new FileOpenAction("Datei öffnen", fileChooser);

    /**
     * Self test just calls the dialogs.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (UnsupportedLookAndFeelException e) {
            // TODO handle exception
        } catch (ClassNotFoundException e) {
            // TODO handle exception
        } catch (InstantiationException e) {
            // TODO handle exception
        } catch (IllegalAccessException e) {
            // TODO handle exception
        }

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new FileManagerTest_2().create();
            }
        });
    }

    public void create() {
        JFrame f = new JFrame();
        f.setTitle("FileManagerTest");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton openButton = new JButton(fileOpenAction.toString());
        openButton.setSize(new Dimension(300, 200));
        openButton.addActionListener(fileOpenAction);

        JPanel buttonPanel = new JPanel(); // FlowLayout
        buttonPanel.add(openButton);

        f.add(buttonPanel, BorderLayout.PAGE_START);
        f.setSize(new Dimension(400, 300));
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
