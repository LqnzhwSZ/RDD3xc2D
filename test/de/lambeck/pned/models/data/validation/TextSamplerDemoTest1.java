package de.lambeck.pned.models.data.validation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.text.*;

/*
 * Based on Oracles "TextSamplerDemo.java"
 */

@SuppressWarnings({ "javadoc", "serial" })
public class TextSamplerDemoTest1 extends JPanel {
    private String newline = "\n";
    protected static final String textFieldString = "JTextField";
    protected static final String ftfString = "JFormattedTextField";
    protected static final String buttonString = "JButton";

    protected JLabel actionLabel;

    public TextSamplerDemoTest1() {
        setLayout(new BorderLayout());

        // Create an info pane.
        JEditorPane infoPane = createInfoPane();
        infoPane.setBackground(Color.YELLOW);
        JScrollPane infoScrollPane = new JScrollPane(infoPane);
        infoScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // infoScrollPane.setPreferredSize(new Dimension(250, 145));
        infoScrollPane.setPreferredSize(new Dimension(200, 150));
        infoScrollPane.setMinimumSize(new Dimension(10, 10));

        JPanel rightPane = new JPanel(new GridLayout(1, 0));
        rightPane.add(infoScrollPane);
        rightPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Styled Text"),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        add(rightPane, BorderLayout.LINE_END);
    }

    // private JEditorPane createInfoPane() {
    // JEditorPane editorPane = new JEditorPane();
    // editorPane.setEditable(true);
    // java.net.URL helpURL =
    // TextSamplerDemoTest.class.getResource("TextSamplerDemoHelp.html");
    // if (helpURL != null) {
    // try {
    // editorPane.setPage(helpURL);
    // } catch (IOException e) {
    // System.err.println("Attempted to read a bad URL: " + helpURL);
    // }
    // } else {
    // System.err.println("Couldn't find file: TextSampleDemoHelp.html");
    // }
    //
    // return editorPane;
    // }

    // private JTextPane createTextPane() {
    private JTextPane createInfoPane() {
        String[] initString = { "This is an editable JTextPane, ",            // regular
                "another ",                                   // italic
                "styled ",                                    // bold
                "text ",                                      // small
                "component, ",                                // large
                "which supports embedded components..." + newline,// regular
                "...and embedded icons..." + newline,         // regular
                newline + "JTextPane is a subclass of JEditorPane that "
                        + "uses a StyledEditorKit and StyledDocument, and provides "
                        + "cover methods for interacting with those objects." };

        String[] initStyles = { "regular", "italic", "bold", "small", "large", "regular", "button", "regular", "icon",
                "regular" };

        JTextPane textPane = new JTextPane();
        StyledDocument doc = textPane.getStyledDocument();
        addStylesToDocument(doc);

        try {
            for (int i = 0; i < initString.length; i++) {
                doc.insertString(doc.getLength(), initString[i], doc.getStyle(initStyles[i]));
            }
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }

        return textPane;
    }

    protected void addStylesToDocument(StyledDocument doc) {
        // Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("TextSamplerDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add content to the window.
        frame.add(new TextSamplerDemoTest1());

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }
}
