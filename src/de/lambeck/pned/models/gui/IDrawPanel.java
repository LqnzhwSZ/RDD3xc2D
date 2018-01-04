package de.lambeck.pned.models.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;

import de.lambeck.pned.application.ApplicationController;
import de.lambeck.pned.elements.ENodeType;
import de.lambeck.pned.elements.gui.IGuiArc;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.elements.gui.IGuiNode;
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
     * Returns the location of the mousePressed event stored by
     * setMousePressedLocation(p).
     * 
     * @return A {@link Point}
     */
    Point getMousePressedLocation();

    /**
     * Stores the specified location as last mousePressed event.
     * 
     * @param p
     *            The specified {@link Point}
     */
    void setMousePressedLocation(Point p);

    /**
     * @return The current "mouseDragMode" state
     */
    boolean getStateMouseDragMode();

    /**
     * Callback for the {@link MyMouseAdapter} to set the "mouseDragMode" state
     * of this {@link IDrawPanel}.
     * 
     * @param b
     *            The new state
     */
    void setStateMouseDragMode(boolean b);

    /**
     * Returns the location from where the current dragging operation has
     * started.
     * 
     * @return A {@link Point}
     */
    Point getInitialDraggedFrom();

    /**
     * Stores where the current dragging operation has started.
     * 
     * @param p
     *            The specified {@link Point}
     */
    void setInitialDraggedFrom(Point p);

    /**
     * Returns the location from where the <B>last step</B> of the current
     * dragging operation has started.
     * 
     * @return A {@link Point}
     */
    Point getMouseDraggedFrom();

    /**
     * Stores where the <B>last step</B> of the current dragging operation has
     * started.
     * 
     * @param p
     *            The specified {@link Point}
     */
    void setMouseDraggedFrom(Point p);

    /**
     * Returns the target of the current dragging operation.
     * 
     * @return A {@link Point}
     */
    Point getMouseDraggedTo();

    /**
     * Stores the target of the current dragging operation.
     * 
     * @param p
     *            The specified {@link Point}
     */
    void setMouseDraggedTo(Point p);

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
     * Handles the request of the {@link MouseAdapter} to check if an element
     * can be selected. (squares, circles and arrows)
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

    /**
     * Tells the draw panel if there is a popup menu active.
     * 
     * @param p
     *            The location of the popup trigger; null if there is no popup
     *            menu
     */
    void setPopupMenuLocation(Point p);

    /**
     * Tells the draw panel that the current popup menu was canceled. (e.g. with
     * ESC or by clicking at somewhere else on the DrawPanel)
     */
    void popupMenuCanceled();

    /**
     * Tells the draw panel that the current popup menu was left. (e.g.
     * "normally" by clicking at a button of the popup menu)
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

    /**
     * Returns the type of node currently set as source for the new
     * {@link IGuiArc} to be added by the {@link IGuiModelController}.
     * 
     * @return The {@link ENodeType} of the node
     */
    ENodeType getSourceForNewArcType();

    public void setZoom(double zoom);
    public double getZoom();



}
