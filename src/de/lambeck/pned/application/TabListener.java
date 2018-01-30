package de.lambeck.pned.application;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lambeck.pned.util.ConsoleLogger;

/**
 * ChangeListener for the JTabbedPane. Changes the application title according
 * to the current tab.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class TabListener implements ChangeListener {

    /** Show debug messages? */
    private static boolean debug = false;

    /** Reference to the {@link ApplicationController} */
    protected ApplicationController appController = null;

    /**
     * Constructs this listener with a reference to the main frame.
     * 
     * @param controller
     *            The application controller
     */
    public TabListener(ApplicationController controller) {
        super();
        this.appController = controller;

        debug = controller.getShowDebugMessages();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        tabstateChanged(e);
    }

    /**
     * Changes the application title according to the current tab.
     * 
     * @param e
     *            The ChangeEvent
     */
    private void tabstateChanged(ChangeEvent e) {
        /* Get the selected tab index */
        JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
        int tabIndex = sourceTabbedPane.getSelectedIndex();

        /* Update the active file in the application controller. */
        appController.setActiveFile(tabIndex); // This might be an actual index
                                               // or -1.

        if (tabIndex >= 0) {
            String message = "TabListener, new active tab index: " + tabIndex;
            ConsoleLogger.logIfDebug(debug, message);
        }
    }

}
