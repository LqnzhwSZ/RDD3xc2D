package de.lambeck.pned.models.gui;

import java.awt.Rectangle;
import java.util.List;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.data.IDataNode;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.elements.gui.IGuiNode;
import de.lambeck.pned.elements.gui.IGuiPlace;
import de.lambeck.pned.elements.gui.IGuiTransition;
import de.lambeck.pned.exceptions.PNNoSuchElementException;
import de.lambeck.pned.models.IModel;
import de.lambeck.pned.models.data.IDataModel;

/**
 * Sub type of {@link IModel} for {@link IGuiModel}. This means models with all
 * the graphical information (from a {@link IDataModel} or drawn by the user).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiModel extends IModel {

    /* Getter and Setter */

    /**
     * Returns a list with all elements in this model.
     * 
     * @return Elements of the petri net
     */
    List<IGuiElement> getElements();

    /**
     * Returns the {@link IGuiElement} with the specified id.
     * 
     * @param id
     *            The id to search for
     * @return The {@link IGuiElement} if found
     * @throws PNNoSuchElementException
     *             If this model has no element with the specified id
     */
    IGuiElement getElementById(String id) throws PNNoSuchElementException;

    /**
     * Returns a list with all selected elements in this model.
     * 
     * @return Elements of the petri net
     */
    List<IGuiElement> getSelectedElements();

    /**
     * Returns the {@link IGuiNode} with the specified id.
     * 
     * @param id
     *            The id to search for
     * @return The {@link IGuiNode} if found
     * @throws PNNoSuchElementException
     *             If this model has no node with the specified id
     */
    IGuiNode getNodeById(String id) throws PNNoSuchElementException;

    /**
     * Returns the {@link IGuiPlace} with the specified id.
     * 
     * @param id
     *            The id to search for
     * @return The {@link IGuiPlace} if found
     * @throws PNNoSuchElementException
     *             If this model has no place with the specified id
     */
    IGuiPlace getPlaceById(String id) throws PNNoSuchElementException;

    /**
     * Returns the {@link IGuiTransition} with the specified id.
     * 
     * @param id
     *            The id to search for
     * @return The {@link IGuiTransition} if found
     * @throws PNNoSuchElementException
     *             If this model has no transition with the specified id
     */
    IGuiTransition getTransitionById(String id) throws PNNoSuchElementException;

    /**
     * Returns the minimum Z value for all elements in this GUI model.<BR>
     * <BR>
     * Note: Intended to be used for putting an element to the background.
     * 
     * @return The minimum z value
     */
    int getMinZValue();

    /**
     * Returns the maximum Z value for all elements in this GUI model.<BR>
     * <BR>
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
     * Returns a new minimum for the z value (height level) of all elements (The
     * lowest level used by existing elements - 1). Returns the current minimum
     * if there are no elements.<BR>
     * <BR>
     * Note: The limit of MinInt should never be reachable during a session.
     * 
     * @return The new minimum z value
     */
    int getDecrMinZ();

    /**
     * Returns a new maximum for the z value (height level) of all elements (The
     * highest level used by existing elements + 1). Returns the current maximum
     * if there are no elements.<BR>
     * <BR>
     * Note: The limit of MaxInt should never be reachable during a session.
     * 
     * @return The new maximum z value
     */
    int getIncrMaxZ();

    /* Sorting (for proper display on the draw panel) */

    /**
     * Sorts the List of elements with ascending z values (Height level).
     */
    void sortElements();

    /**
     * Sorts the List of selected elements with ascending z values (Height
     * level).
     */
    void sortSelectedElements();

    /* Methods for adding, modify and removal of elements */

    /**
     * Sets the selection to only 1 {@link IGuiElement}.<BR>
     * <BR>
     * Note that this method calls clearSelection() first. The previous
     * selection gets cleared if element is null.
     * 
     * @param element
     *            The element to select
     */
    void selectSingleElement(IGuiElement element);

    /**
     * Toggles the selection of this {@link IGuiElement}.
     * 
     * @param element
     *            The element whose isSelected state has to be switched
     */
    void toggleSelection(IGuiElement element);

    /**
     * Adds the specified {@link IGuiElement} to the {@link List} of selected
     * elements.
     * 
     * @param element
     *            The {@link IGuiElement} to add
     */
    void addToSelection(IGuiElement element);

    /**
     * Clears the whole selection.
     */
    void clearSelection();

    /* Validation events */

    /**
     * Handles the {@link IGuiModelController} request to update the start place
     * on the draw panel.
     * 
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as start place; otherwise false
     */
    void setGuiStartPlace(String placeId, boolean b);

    /**
     * Handles the {@link IGuiModelController} request to update the start place
     * candidate on the draw panel.
     * 
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as start place candidate; otherwise false
     */
    void setGuiStartPlaceCandidate(String placeId, boolean b);

    /**
     * Handles the {@link IGuiModelController} request to update the end place
     * on the draw panel.
     * 
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as end place; otherwise false
     */
    void setGuiEndPlace(String placeId, boolean b);

    /**
     * Handles the {@link IGuiModelController} request to update the end place
     * candidate on the draw panel.
     * 
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as end place candidate; otherwise false
     */
    void setGuiEndPlaceCandidate(String placeId, boolean b);

    /**
     * Handles the {@link IGuiModelController} request to update the status of
     * the specified GUI node on the draw panel.
     * 
     * @param nodeId
     *            The id of the {@link IDataNode}
     * @param b
     *            True = unreachable; False = can be reached from the start
     *            place and can reach the end place
     */
    void highlightUnreachableGuiNode(String nodeId, boolean b);

    /* For the "draw new arc" overlay */

    /**
     * Adds the specified {@link IOverlay} (for paintable elements) to this GUI
     * model.
     * 
     * @param overlay
     *            The {@link IOverlay} to add
     * @param name
     *            The {@link EOverlayName} for the specified overlay
     */
    void addOverlay(IOverlay overlay, EOverlayName name);

    /**
     * Removes the specified overlay from this GUI model.
     * 
     * @param name
     *            The name of the {@link IOverlay} to remove
     */
    void removeOverlay(EOverlayName name);

    /**
     * Returns a list with all overlays on this {@link IGuiModel}.<BR>
     * <BR>
     * Note: This should be used by the {@link IDrawPanel} to retrieve all
     * overlays that may contain paintable elements.
     * 
     * @return {@link List} of type {@link IOverlay}
     */
    List<IOverlay> getAllOverlays();

    /**
     * Returns the specified overlay from this {@link IGuiModel}s overlays.
     * 
     * @param name
     *            The {@link EOverlayName} of the overlay
     * @return The specified {@link IOverlay}
     */
    IOverlay getOverlayByName(EOverlayName name);

    /* Switching between files */

    /**
     * To be called for cleanup of an {@link IGuiModel} after it got
     * deactivated.
     */
    void deactivated();

    /**
     * Returns the area of the start place in this {@link IGuiModel}.
     * 
     * @return A {@link Rectangle}; null if this {@link IGuiModel} has no real
     *         (unambiguous) start place
     */
    Rectangle getStartPlaceArea();

    /**
     * Returns a {@link List} with the areas of all enabled
     * {@link IGuiTransition} in this {@link IGuiModel}.
     * 
     * @return A {@link List} of type {@link Rectangle}; empty List if this
     *         {@link IGuiModel} has no enabled {@link IGuiTransition}
     */
    List<Rectangle> getEnabledTransitionsAreas();

}
