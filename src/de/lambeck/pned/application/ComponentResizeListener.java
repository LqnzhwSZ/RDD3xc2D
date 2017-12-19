package de.lambeck.pned.application;

import java.awt.event.ComponentEvent;

/*
 * https://docs.oracle.com/javase/tutorial/uiswing/events/componentlistener.html
 */

/**
 * Implements a component resize listener for the Petri net editor. Holds a
 * reference to the application controller to be able to report the
 * componentResized event.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ComponentResizeListener extends ComponentAdapter {

    /*
     * The application controller
     */
    private ApplicationController appController = null;

    /**
     * Constructs the ComponentResizeListener with a reference to the
     * application controller.
     * 
     * @param controller
     *            The application controller
     */
    public ComponentResizeListener(ApplicationController controller) {
        super();
        this.appController = controller;
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
        appController.componentResized(arg0);
    }

}
