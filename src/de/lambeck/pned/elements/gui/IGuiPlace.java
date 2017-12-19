package de.lambeck.pned.elements.gui;

import java.awt.Point;

import de.lambeck.pned.elements.data.EPlaceMarking;

/**
 * Interface for places with a "marking" circle that can be de-/activated with
 * mouse clicks.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiPlace extends IGuiNode {

    /**
     * Getter for the marking of this place.
     * 
     * @return The marking
     */
    EPlaceMarking getMarking();

    /**
     * Setter for the marking of this place.
     * 
     * @param newMarking
     *            specifies if the place gets a marking
     */
    void setMarking(EPlaceMarking newMarking);

    /**
     * Checks if the point is within the "marking" circle of this
     * {@link GuiPlace}.
     * 
     * Note: Use the result in the GuiManager to (re)set the attribute "marking"
     * of this place.
     * 
     * @param p
     *            the Point to check
     * @return true if the Point is within the marking circle; otherwise false
     */
    boolean markingCircleContains(Point p);

}
