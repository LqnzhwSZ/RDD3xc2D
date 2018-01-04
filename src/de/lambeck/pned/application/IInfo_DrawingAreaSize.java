package de.lambeck.pned.application;

/**
 * Interface for components that can show the current size of the drawing area
 * (or can pass it to a parent component).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IInfo_DrawingAreaSize {

    /**
     * Sets the current size of the drawing area.
     * 
     * @param width
     *            The current width; set to &lt;0 if draw panel is null
     * @param height
     *            The current height; set to &lt;0 if draw panel is null
     */
    void setInfo_DrawingAreaSize(int width, int height);

}
