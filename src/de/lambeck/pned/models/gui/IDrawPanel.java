package de.lambeck.pned.models.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.elements.gui.IGuiArc;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.gui.popupMenu.PopupMenuManager;
import de.lambeck.pned.gui.statusBar.StatusBar;

/**
 * Interface for draw panels for GUI models.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IDrawPanel {

    /**
     * Returns the full name of this draw panel. This should be the canonical
     * (unique) path name of the file.
     * 
     * @return The full name
     */
    String getModelName();

    /**
     * Returns the display name of this draw panel.
     * 
     * Intended use: name of the tab
     * 
     * @return The display name
     */
    String getDisplayName();

    // /**
    // * Returns the size of this draw panel.
    // *
    // * Note: Used in {@link ApplicationController} to update the
    // * {@link StatusBar}.
    // *
    // * @return {@link Dimension}
    // */
    // Dimension getSize();

    /**
     * Returns the preferred size of this draw panel.
     * 
     * Note: Used in {@link ApplicationController} to update the
     * {@link StatusBar}.
     * 
     * @return {@link Dimension}
     */
    Dimension getPreferredSize();

    /**
     * Tells the draw panel to update its content.
     * 
     * @param area
     *            The area to update; set to null for complete repaint
     */
    void updateDrawing(Rectangle area);

    /**
     * Resets the state of this {@link DrawPanel} (e.g. Cursor, old mouse
     * operations interrupted by tab switching etc.)
     * 
     * Note: Otherwise, a draw panel with the "moveCursor" could be left via tab
     * switch using the mouse. And when the user re-enters this tab, there would
     * still be the moveCursor active even without pressing ALT on the keyboard!
     */
    void resetState();

    /**
     * Handles the request of the {@link PopupMenuManager} if an element can be
     * selected. (squares, circles and arrows)
     * 
     * Passes this request to the GUI controller.
     * 
     * @param element
     *            The element to check
     * @return True if the element can be selected; otherwise false
     */
    boolean isSelectableElement(IGuiElement element);

    /**
     * Returns the minimum Z value for all elements in this draw panels GUI
     * model.
     * 
     * Note: Intended to be used for putting an element to the background.
     * 
     * @return The minimum z value
     */
    int getMinZValue();

    /**
     * Returns the maximum Z value for all elements in this draw panels GUI
     * model.
     * 
     * Note: Intended to be used for putting an element to the foreground.
     * 
     * @return The maximum z value
     */
    int getMaxZValue();

    /**
     * Returns the z value of an element
     * 
     * @param element
     *            The current element
     * @return The z value (height level)
     */
    int getZValue(IGuiElement element);

    // /**
    // * Tells the draw panel if there is a popup menu active.
    // *
    // * @param b
    // * The new state of this attribute
    // */
    // void setPopupMenuActive(boolean b);

    /**
     * Tells the draw panel if there is a popup menu active.
     * 
     * @param p
     *            The location of the popup trigger; null if there is no popup
     *            menu
     */
    void setPopupMenuLocation(Point p);

    /**
     * Tells the draw panel that the current popup menu was cancelled. (e.g.
     * with ESC)
     */
    void popupMenuCanceled();

    /**
     * Tells the draw panel that the current popup menu was left. (e.g. by
     * clicking at one of the Actions in the popup menu)
     */
    void popupMenuLeft();

    /**
     * Callback for the {@link IGuiModelController}.
     * 
     * @return The popup trigger location
     */
    Point getPopupMenuLocation();

    /**
     * Checks if the {@link IGuiModelController} is currently in the state to
     * add a new {@link IGuiArc}.
     * 
     * @return True if the GUI model controller is waiting for the second
     *         {@link IGuiNode} to finish the Arc; otherwise false.
     */
    boolean getStateAddingNewArc();

}
