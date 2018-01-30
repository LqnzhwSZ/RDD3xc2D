package de.lambeck.pned.elements.gui;

import java.awt.Point;
import java.awt.Rectangle;

import de.lambeck.pned.elements.IElement;
import de.lambeck.pned.models.gui.IGuiModel;

/**
 * Sub type of IElement for elements in a {@link IGuiModel}.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiElement extends IPaintable, IElement, IHasZValue, ISelectable {

    /**
     * Checks if a Point is within the shape.<BR>
     * <BR>
     * Note that this refers to the shape of this node itself (place,
     * transition) only. It does not take the area of the label into account!
     * 
     * @param p
     *            the Point to check
     * @return true if the Point is within the shape
     */
    boolean contains(Point p);

    /**
     * Returns the drawing area used during last invocation of paintElement.
     * 
     * @return The last drawing area as {@link Rectangle}
     */
    Rectangle getLastDrawingArea();

}
