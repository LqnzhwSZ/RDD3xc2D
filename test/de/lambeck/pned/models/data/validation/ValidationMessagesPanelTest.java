package de.lambeck.pned.models.data.validation;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

@SuppressWarnings("javadoc")
public class ValidationMessagesPanelTest {

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("ValidationMessagesAreaTest");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
         * Add the validation info panel
         */
        IValidationMessagesPanel area = new ValidationMessagesPanel("modelName", "displayName");
        frame.add((Component) area);

        /*
         * Add some text
         */
        area.addMessage("Das ist ein Test.");
        area.addMessage("Zeile 2");
        area.addMessage("Zeile 3");
        area.addMessage("Zeile 4");
        area.addMessage("Zeile 5");
        area.addMessage("Zeile 6");
        area.addMessage("Zeile 7");
        area.addMessage("Zeile 8");
        area.addMessage("Zeile 9");
        area.addMessage("Zeile 10");
        area.addMessage("Zeile 11");
        area.addMessage("Zeile 12");
        area.addMessage("Zeile 13");
        area.addMessage("Zeile 14");
        area.addMessage("Zeile 15");
        area.addMessage("Zeile 16");
        area.addMessage("Zeile 17");
        area.addMessage("Zeile 18");
        area.addMessage("Zeile 19");
        area.addMessage("Zeile 20");
        area.addMessage("Zeile 21");
        area.addMessage("Zeile 22");
        area.addMessage("Zeile 23");
        area.addMessage("Zeile 24");
        area.addMessage("Zeile 25");

        /*
         * Attributes
         */
        area.setBgColor(ValidationColor.PENDING);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    // The standard main method.
    public static void main(String[] args) {
        // Schedule a job for the event dispatch thread:
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
