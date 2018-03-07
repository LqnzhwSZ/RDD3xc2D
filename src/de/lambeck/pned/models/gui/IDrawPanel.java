package de.lambeck.pned.models.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

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
     * Returns the display name of this draw panel.<BR>
     * <BR>
     * Intended use: name of the tab
     * 
     * @return The display name
     */
    String getDisplayName();

    /**
     * Returns the preferred size of this draw panel.<BR>
     * <BR>
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
     * Returns whether the mouse is in drag mode or not.
     * 
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
     * Updates the mouse position info on the status bar.
     * 
     * @param pos
     *            The new mouse position as {@link Point}
     */
    void updateMousePos(Point pos);

    /**
     * Returns the state of the CTRL key on this {@link IDrawPanel}.
     * 
     * @return True = CTRL pressed, false = CTRL not pressed
     */
    boolean getCtrlKeyPressed();

    /**
     * Sets the tool tip text for the current mouse position on this
     * {@link DrawPanel}.
     * 
     * @param text
     *            The tool tip text
     */
    void setToolTipText(String text);

    /**
     * Resets the state of this {@link DrawPanel} (e.g. Cursor, old mouse
     * operations interrupted by tab switching etc.)<BR>
     * <BR>
     * Note: Otherwise, a draw panel with the "moveCursor" could be left via tab
     * switch using the mouse. And when the user re-enters this tab, there would
     * still be the moveCursor active even without pressing ALT on the keyboard!
     */
    void resetState();

    /**
     * Returns the minimum Z value (height level) over all elements in this draw
     * panels current {@link IGuiModel}.<BR>
     * <BR>
     * Note: Intended to be used for putting an element to the background.
     * 
     * @return The minimum Z value
     */
    int getMinZValue();

    /**
     * Returns the maximum Z value (height level) over all elements in this draw
     * panels current {@link IGuiModel}.<BR>
     * <BR>
     * Note: Intended to be used for putting an element to the foreground.
     * 
     * @return The maximum Z value
     */
    int getMaxZValue();

    /**
     * Returns the Z value (height level) of the specified {@link IGuiElement}.
     *
     * @param element
     *            The specified {@link IGuiElement}
     * @return The Z value
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
     * Callback for the {@link IGuiModelController}.
     * 
     * @return The popup trigger location
     */
    Point getPopupMenuLocation();

    /* For the "draw new arc" overlay */

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

    /**
     * Handles the request to activate the "draw new arc" mode for the
     * {@link MyMouseAdapter} on this draw panel.
     */
    void activateDrawArcMode();

    /**
     * Handles the request to deactivate the "draw new arc" mode for the
     * {@link MyMouseAdapter} on this draw panel.
     */
    void deactivateDrawArcMode();

}
