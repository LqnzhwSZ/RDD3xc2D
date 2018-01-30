package de.lambeck.pned.elements.gui;

// TODO Move getLastDrawingArea() to IPaintable?

import java.awt.Point;
import java.awt.Rectangle;

import de.lambeck.pned.models.gui.IDrawArcOverlay;

/**
 * Interface for the arc in the {@link IDrawArcOverlay}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IOverlayGuiArc extends IPaintable {

    /**
     * Sets the (new) current end location of this {@link IGuiArc}.<BR>
     * <BR>
     * Note: The start node is a constructor parameter.
     * 
     * @param p
     *            The new end location
     */
    void setCurrentArcEndLocation(Point p);

    /**
     * Returns the drawing area used during last invocation of paintElement.
     * 
     * @return The last drawing area as {@link Rectangle}
     */
    Rectangle getLastDrawingArea();

}
