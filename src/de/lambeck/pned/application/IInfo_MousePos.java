package de.lambeck.pned.application;

import java.awt.Point;

/**
 * Interface for components that can show the current mouse position (or can
 * pass it to a parent component).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IInfo_MousePos {

    /**
     * Sets the current mouse position.
     * 
     * @param p
     *            The current mouse position
     */
    void setInfo_MousePos(Point p);

}
