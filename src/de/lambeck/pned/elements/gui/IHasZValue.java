package de.lambeck.pned.elements.gui;

/**
 * Interface for elements with a z-value (height) level
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IHasZValue {

    /**
     * Getter for the z level of this node.
     * 
     * @return ZValue
     */
    int getZValue();

    /**
     * Setter for the z level of this node.
     * 
     * @param newZValue
     *            The new z value
     */
    void setZValue(int newZValue);

}
