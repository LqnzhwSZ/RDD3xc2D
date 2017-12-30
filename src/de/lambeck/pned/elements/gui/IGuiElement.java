package de.lambeck.pned.elements.gui;

import java.awt.Graphics;
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
public interface IGuiElement extends IElement, IHasZValue, ISelectable {

    /**
     * Paints this element.
     * 
     * @param g
     *            The graphics context of this node.
     */
    void paintElement(Graphics g);

    /**
     * Checks if a Point is within the shape.
     * 
     * Note that this refers to the shape of this node itself (place,
     * transition) only. It does not take the area of the label into account!
     * 
     * @param p
     *            the Point to check
     * @return true if the Point is within the shape
     */
    boolean contains(Point p);

    // /**
    // * Returns the area that is used when this element is painted.
    // *
    // * Note: This should be an exact value for nodes (places and transitions).
    // * But only an approximation for arcs.
    // *
    // * @return The area in which the element is painted
    // */
    // Rectangle getApproxDrawArea();

    /**
     * Returns the drawing area used during last invocation of paintElement.
     * 
     * @return The last drawing area as {@link Rectangle}
     */
    Rectangle getLastDrawingArea();

}
