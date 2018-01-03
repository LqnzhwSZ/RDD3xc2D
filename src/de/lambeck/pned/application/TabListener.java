package de.lambeck.pned.application;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * ChangeListener for the JTabbedPane. Changes the application title according
 * to the current tab.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class TabListener implements ChangeListener {

    private static boolean debug = false;

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
        /*
         * Get the selected tab
         */
        JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
        int tabIndex = sourceTabbedPane.getSelectedIndex();

        appController.setActiveFile(tabIndex); // This is an actual index or -1.

        if (tabIndex < 0) {
            appController.setFilenameOnTitle("");
            return;
        } else {
            String filename = sourceTabbedPane.getTitleAt(tabIndex);
            appController.setFilenameOnTitle(filename);
        }

        if (debug) {
            System.out.println("TabListener, new active tab index: " + tabIndex);
        }
    }

}
