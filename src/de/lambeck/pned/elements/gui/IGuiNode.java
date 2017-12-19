package de.lambeck.pned.elements.gui;

import java.awt.Point;

import de.lambeck.pned.elements.INode;

/**
 * Sub type of INode for GUI models.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiNode extends IGuiElement, INode {

    /*
     * No adding and removal of elements - the GUI nodes do not need to know
     * their predecessors/successors!
     */

    /*
     * For the crop area when repainting the draw panel.
     */

    /**
     * Returns the x value for the left boundary of the surrounding area.
     * 
     * Note that this may be different from the shapes left boundary because it
     * includes the area of the label.
     * 
     * @return the x value for the left boundary
     */
    int getTotalLeftX();

    /**
     * Returns the y value for the top boundary of the surrounding area.
     * 
     * Note that this may be different from the shapes top boundary because it
     * includes the area of the label.
     * 
     * @return the y value for the top boundary
     */
    int getTotalTopY();

    /**
     * Returns the width of the surrounding area.
     * 
     * Note that this may be different from the shapes width because it includes
     * the area of the label.
     * 
     * @return the y value for the top boundary
     */
    int getTotalWidth();

    /**
     * Returns the height of the surrounding area.
     * 
     * Note that this may be different from the shapes height because it
     * includes the area of the label.
     * 
     * @return the y value for the top boundary
     */
    int getTotalHeight();

    /*
     * For the positioning of arcs
     */

    /**
     * This method returns the anchor location for an arc from/towards the node.
     * This anchor is located at the border of the node.
     * 
     * @param target
     *            The target/source of the arc
     * @return The position for start/end of an arc
     */
    Point getArcAnchor(Point target);

}
