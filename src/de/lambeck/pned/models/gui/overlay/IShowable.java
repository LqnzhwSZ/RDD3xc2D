package de.lambeck.pned.models.gui.overlay;

/*
 * DrawArcOverlay is working but without this interface.
 */

/**
 * Interface for Components that can be visible or invisible.<BR>
 * <BR>
 * Note: Non-public interface
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
interface IShowable {

    /* Getter and Setter */

    /**
     * Indicates whether this Component is visible at the moment or not.
     * 
     * @return True = visible, false = not visible
     */
    boolean isVisible();

    /**
     * Shows the component (visible = true).
     */
    void show();

    /**
     * Hides the component (visible = false).
     */
    void hide();

}
