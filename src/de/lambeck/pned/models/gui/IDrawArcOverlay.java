package de.lambeck.pned.models.gui;

import java.awt.Point;

import de.lambeck.pned.elements.gui.IGuiArc;
import de.lambeck.pned.elements.gui.IOverlayGuiArc;

/**
 * Interface for {@link DrawArcOverlay}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IDrawArcOverlay extends IOverlay {

    /**
     * Sets the (new) current end location of the {@link IGuiArc} on this
     * overlay.<BR>
     * <BR>
     * Note: The start node is a constructor parameter.
     * 
     * @param p
     *            The new end location
     */
    void setCurrentArcEndLocation(Point p);

    /**
     * Getter for the current {@link IGuiArc} on this overlay.
     * 
     * @return The arc as {@link IGuiArc}
     */
    IOverlayGuiArc getCurrentArc();

}
