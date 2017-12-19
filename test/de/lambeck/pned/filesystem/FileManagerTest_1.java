package de.lambeck.pned.filesystem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.*;

@SuppressWarnings({ "javadoc", "serial" })
public class FileManagerTest_1 extends JPanel {

    private static final int SHORTCUT_KEY_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    private JFileChooser fileChooser = new JFileChooser();
    private Action fileOpenAction = new FileOpenAction("Datei öffnen");

    /**
     * Self test just calls the dialogs.
     * 
     * @param args
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new FileManagerTest_1().create();
            }
        });
    }

    // private void doTest() {
    // FileManager fm = new FileManager();
    // System.out.println(fm.getOpenFileName());
    // System.out.println(fm.getSaveAsFileName());
    // }

    public void create() {
        JFrame f = new JFrame();
        f.setTitle("FileManagerTest");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton openButton = new JButton("oeffnen");
        openButton.setSize(new Dimension(300, 200));

        // openButton.addActionListener(new ActionListener() {
        //
        // @Override
        // public void actionPerformed(ActionEvent e) {
        //
        // }
        // });

        openButton.addActionListener(fileOpenAction);

        JPanel buttonPanel = new JPanel(); // FlowLayout
        buttonPanel.add(openButton);

        f.add(buttonPanel, BorderLayout.PAGE_START);
        f.setSize(new Dimension(400, 300));
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private class FileOpenAction extends AbstractAction {

        public FileOpenAction(String name) {
            super(name);
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, SHORTCUT_KEY_MASK));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Handle open button action.
            int returnVal = fileChooser.showOpenDialog(FileManagerTest_1.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                // This is where a real application would open the file.
                System.out.println("Opening: " + file.getName() + ".");
            } else {
                System.out.println("Open command cancelled by user.");
            }
        }
    }
}
