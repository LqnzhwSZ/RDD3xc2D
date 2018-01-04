package de.lambeck.pned.application;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/*
 * https://docs.oracle.com/javase/tutorial/uiswing/events/componentlistener.html
 */

/**
 * Implements an adapter for {@link ComponentListener}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public class ComponentAdapter implements ComponentListener {

    @Override
    public void componentHidden(ComponentEvent arg0) {
        // Adapter without implementation
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
        // Adapter without implementation
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
        // Adapter without implementation
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
        // Adapter without implementation
    }

}
