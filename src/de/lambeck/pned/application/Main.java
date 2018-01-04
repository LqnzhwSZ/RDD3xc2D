package de.lambeck.pned.application;

import java.awt.Dimension;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.lambeck.pned.gui.statusBar.StatusBar;
import de.lambeck.pned.i18n.I18NManager;

/**
 * Application start. Can use the language code and country code specified by
 * parameter 1 and 2 for the GUI. Otherwise standard: German.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class Main {

    /** Show debug messages? */
    private static boolean debug = false;

    /** The initial title of the application */
    private static String initialTitle = "Petri net Editor - Thomas Lambeck, MatrNr. 4128320";

    /** The minimum size of the main application window */
    private static Dimension minSize = new Dimension(400, 300);

    /** The current Locale (language) from the command line parameters */
    private static Locale currentLocale;

    /**
     * Create the GUI for a specified language and country and show it.
     * 
     * @param locale
     *            The Locale for creation of the GUI
     */
    private static void createAndShowGUI(Locale locale) {
        try {
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");

            // Set System L&F
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (UnsupportedLookAndFeelException e) {
            // TODO handle exception
        } catch (ClassNotFoundException e) {
            // TODO handle exception
        } catch (InstantiationException e) {
            // TODO handle exception
        } catch (IllegalAccessException e) {
            // TODO handle exception
        }

        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        /* Create and set up the window. */
        JFrame frame = new JFrame(initialTitle);
        frame.setMinimumSize(minSize);

        /*
         * Get a few objects (like the manager for localized strings) that are
         * needed for application controller, draw panel etc.
         */
        I18NManager i18n = new I18NManager(locale);
        StatusBar statusBar = new StatusBar(i18n);

        /* Add the application controller. */
        @SuppressWarnings("unused")
        ApplicationController appController = new ApplicationController(frame, i18n, statusBar);

        /* Display the window. */
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Starts the application. Can use the language and country code specified
     * by parameter 1 and 2 for the GUI. (Otherwise standard: Deutsch)
     * 
     * @param args
     *            Language code and country code for the GUI
     */
    public static void main(String[] args) {
        /* Determine the Locale from the first two parameters. */
        currentLocale = getLocale(args);

        /* Test: Simulate different start parameters */
        // currentLocale = new Locale("en", "GB");

        if (debug) {
            System.out.println("Application start with Locale: " + currentLocale);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI(currentLocale);
            }
        });
    }

    /**
     * Returns a Locale from parameters 1 and 2. (Fallback value: German)
     * 
     * @param args
     *            Language code and country code for the GUI
     * @return The Locale (or fallback: German)
     */
    private static Locale getLocale(String[] args) {
        Locale fallBack = new Locale("de", "DE");
        if (args.length < 2)
            return fallBack;

        String language = new String(args[0]);
        String country = new String(args[1]);
        Locale locale = new Locale(language, country);
        return locale;
    }

}
