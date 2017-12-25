package de.lambeck.pned.models.gui;

import java.util.List;
import java.util.NoSuchElementException;

import de.lambeck.pned.elements.data.DataPlace;
import de.lambeck.pned.elements.gui.IGuiElement;
import de.lambeck.pned.models.IModel;

/**
 * Sub type of IModel for GUI models. This means models with all the graphical
 * information (from a data model or drawn by the user).
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
public interface IGuiModel extends IModel {

    /*
     * Setter and Getter
     */

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
     * @return The element if found
     * @throws NoSuchElementException
     *             if element was not found
     */
    IGuiElement getElementById(String id) throws NoSuchElementException;

    /**
     * Returns a list with all selected elements in this model.
     * 
     * @return Elements of the petri net
     */
    List<IGuiElement> getSelectedElements();

    /**
     * Returns the minimum Z value for all elements in this GUI model.
     * 
     * Note: Intended to be used for putting an element to the background.
     * 
     * @return The minimum z value
     */
    int getMinZValue();

    /**
     * Returns the maximum Z value for all elements in this GUI model.
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
     * Returns a new minimum for the z value (height level) of all elements (The
     * lowest level used by existing elements - 1). Returns the current minimum
     * if there are no elements.
     * 
     * Note: The limit of MinInt should never be reachable during a session.
     * 
     * @return The new minimum z value
     */
    int getDecrMinZ();

    /**
     * Returns a new maximum for the z value (height level) of all elements (The
     * highest level used by existing elements + 1). Returns the current maximum
     * if there are no elements.
     * 
     * Note: The limit of MaxInt should never be reachable during a session.
     * 
     * @return The new maximum z value
     */
    int getIncrMaxZ();

    /*
     * Sorting (for proper display on the draw panel)
     */

    /**
     * Sorts the List of elements with ascending z values (Height level).
     */
    void sortElements();

    /**
     * Sorts the List of selected elements with ascending z values (Height
     * level).
     */
    void sortSelectedElements();

    /*
     * Methods for adding, modify and removal of elements
     */

    /**
     * Sets the selection to only 1 {@link IGuiElement}.
     * 
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
     * Clears the whole selection.
     */
    void clearSelection();

    /*
     * Validation events
     */

    /**
     * Handles the {@link IGuiModelController} request to update the start place
     * on the draw panel.
     * 
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as start place; otherwise false
     */
    void setStartPlace(String placeId, boolean b);

    /**
     * Handles the {@link IGuiModelController} request to update the end place
     * on the draw panel.
     * 
     * @param placeId
     *            The id of the {@link DataPlace}
     * @param b
     *            True to set as end place; otherwise false
     */
    void setEndPlace(String placeId, boolean b);

}
