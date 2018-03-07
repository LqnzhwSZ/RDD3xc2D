package de.lambeck.pned.models.data;

// TODO Not necessary if we clone the whole model via DeepCopy.

import java.util.List;

import de.lambeck.pned.elements.data.IDataElement;

/**
 * Interface for an {@link IDataModel} with Undo/Redo capabilities<BR>
 * <BR>
 * Note: Visibility is limited to this package to prevent external classes from
 * interfering with Undo/Redo operations because the
 * {@link IDataModelController} has to write elements back into the
 * {@link IDataModel} without further plausibility checks.
 * 
 * @author Thomas Lambeck, 4128320
 *
 */
interface IUndoableModel {

    /* Getter */

    /**
     * Returns a list with all {@link IDataElement} in this {@link IDataModel}.
     * 
     * @return all {@link IDataElement} as Java {@link List}
     */
    List<IDataElement> getElements();

    /* Setter */

    /**
     * Removes all {@link IDataElement} from this {@link IDataModel}.
     */
    void clear();

    /**
     * Sets the specified list of {@link IDataElement} as new list of elements
     * in this {@link IDataModel}.
     * 
     * @param newElements
     *            the new Java {@link List} of {@link IDataElement}
     */
    void setElements(List<IDataElement> newElements);

}
