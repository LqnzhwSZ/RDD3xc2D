package de.lambeck.pned.application;

/**
 * Interface for components that can show the current size of the selection
 * range (or can pass it to a parent component).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IInfo_SelectionRangeSize {

    /**
     * Sets the current size of the selection range.
     * 
     * @param width
     *            The current width
     * @param height
     *            The current height
     */
    void setInfo_SelectionRangeSize(int width, int height);

}
