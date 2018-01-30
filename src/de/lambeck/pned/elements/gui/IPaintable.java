package de.lambeck.pned.elements.gui;

// TODO Move all getLastDrawingArea() to here?

import java.awt.Graphics;

import de.lambeck.pned.models.gui.IDrawPanel;

/**
 * Interface for elements with a paint method. (For use on the
 * {@link IDrawPanel})
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IPaintable {

    /**
     * Paints this element.
     * 
     * @param g
     *            The graphics context of this element.
     */
    void paintElement(Graphics g);

}
