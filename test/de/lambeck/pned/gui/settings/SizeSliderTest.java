package de.lambeck.pned.gui.settings;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Self test of the {@link SizeSlider}
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class SizeSliderTest {

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("SliderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SizeSlider slider = new SizeSlider("Shape size", null);

        frame.add(slider, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Self test
     * 
     * @param args
     *            Start parameters will be ignored
     */
    public static void main(String[] args) {
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
