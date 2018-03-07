package de.lambeck.pned.models.gui;

// TODO Not necessary if we clone the whole model via DeepCopy.

import java.util.List;

import de.lambeck.pned.elements.gui.IGuiElement;

/**
 * Interface for an {@link IGuiModel} with Undo/Redo capabilities<BR>
 * <BR>
 * Note: Visibility is limited to this package to prevent external classes from
 * interfering with Undo/Redo operations because the {@link IGuiModelController}
 * has to write elements back into the {@link IGuiModel} without further
 * plausibility checks.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
interface IUndoableModel {

    /* Getter */

    /**
     * Returns a list with all {@link IGuiElement} in this {@link IGuiModel}.
     * 
     * @return all {@link IGuiElement} as Java {@link List}
     */
    List<IGuiElement> getElements();

    /**
     * Returns a list with all selected {@link IGuiElement} in this
     * {@link IGuiModel}.
     * 
     * @return all selected {@link IGuiElement} as Java {@link List}
     */
    List<IGuiElement> getSelectedElements();

    /* Setter */

    /**
     * Removes all {@link IGuiElement} from this {@link IGuiModel}.
     */
    void clear();

    /**
     * Sets the specified list of {@link IGuiElement} as new list of elements in
     * this {@link IGuiModel}.
     * 
     * @param newElements
     *            the new Java {@link List} of {@link IGuiElement}
     */
    void setElements(List<IGuiElement> newElements);

    /**
     * Sets the specified list of {@link IGuiElement} as new list of selected
     * elements in this {@link IGuiModel}.
     * 
     * @param newSelected
     *            the new Java {@link List} of selected {@link IGuiElement}
     */
    void setSelectedElements(List<IGuiElement> newSelected);

}
