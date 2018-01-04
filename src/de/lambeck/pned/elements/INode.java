package de.lambeck.pned.elements;

import java.awt.Point;

/**
 * Interface for all nodes (places or transitions) in the Petri net.
 * 
 * @author Thomas Lambeck, 4128320
 * 
 */
public interface INode extends IElement {

    /* Getter and Setter */

    /**
     * Returns the name of this node.
     * 
     * @return The name
     */
    String getName();

    /**
     * Sets the name of this node.
     * 
     * @param newName
     *            The new name
     */
    void setName(String newName);

    /**
     * Returns the position (center) of this node.<BR>
     * <BR>
     * Note that for the GUI "center" refers to the shape of this node itself
     * (place, transition). It does not include the label.
     * 
     * @return The position
     */
    Point getPosition();

    /**
     * Sets the position (center) of this node.<BR>
     * <BR>
     * Note that for the GUI "center" refers to the shape of this node itself
     * (place, transition). It does not include the label.
     * 
     * @param newPosition
     *            The new position
     */
    void setPosition(Point newPosition);

}
